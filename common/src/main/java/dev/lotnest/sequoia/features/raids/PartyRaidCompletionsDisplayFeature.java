/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.raids;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Handlers;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.type.MessageType;
import com.wynntils.mc.event.TitleSetTextEvent;
import com.wynntils.models.raid.event.RaidEndedEvent;
import com.wynntils.models.raid.event.RaidStartedEvent;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.mc.StyledTextUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Services;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;

public class PartyRaidCompletionsDisplayFeature extends Feature {
    private static final Pattern PARTY_LIST_ALL = Pattern.compile("§e.*Party members: (.*)");

    private boolean shownRaidCompletionsForCurrentParty = false;
    private boolean expectingPartyListMessage = false;

    public enum PartyRaidCompletionsDisplayType {
        MANUAL,
        AUTOMATIC,
        DISABLED
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidStarted(RaidStartedEvent event) {
        SequoiaMod.debug("RaidStartedEvent");
        expectingPartyListMessage = true;
        Handlers.Command.queueCommand("party list");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatReceived(ChatMessageReceivedEvent event) {
        if (event.getMessageType() != MessageType.FOREGROUND) return;
        if (!expectingPartyListMessage) return;

        SequoiaMod.debug("Received chat message while expecting party list message");

        List<String> partyMembers = tryParsePartyList(event.getOriginalStyledText());
        if (partyMembers.isEmpty()) return;

        event.setCanceled(true);
        expectingPartyListMessage = false;

        partyMembers.forEach(partyMember -> {
            switch (SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType()) {
                case MANUAL -> handleManualDisplay(partyMember);
                case AUTOMATIC -> handleAutomaticDisplay(partyMember);
                default -> throw new IllegalStateException("Unexpected value: "
                        + SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType());
            }
        });
        shownRaidCompletionsForCurrentParty = true;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidCompletedEvent(RaidEndedEvent.Completed event) {
        shownRaidCompletionsForCurrentParty = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidFailedEvent(RaidEndedEvent.Failed event) {
        shownRaidCompletionsForCurrentParty = false;
    }

    private void handleManualDisplay(String playerName) {
        McUtils.sendMessageToClient(
                Component.translatable("sequoia.feature.partyRaidCompletionsDisplayFeature.clickToView")
                        .withStyle(style -> style.withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/seq playerRaids " + playerName))));
    }

    private void handleAutomaticDisplay(String playerName) {
        Services.Player.getPlayer(playerName).thenAccept(playerResponse -> {
            if (playerResponse == null) {
                return;
            }

            McUtils.sendMessageToClient(SequoiaMod.prefix(Component.translatable(
                            "sequoia.command.playerRaids.showingPlayerRaids",
                            playerResponse.getUsername(),
                            playerResponse.getGlobalData().getRaids().getTotal())
                    .append("\n")
                    .append(playerResponse.getGlobalData().getRaids().toPrettyMessage(playerResponse.getRanking()))));
        });
    }

    private List<String> tryParsePartyList(StyledText styledText) {
        SequoiaMod.debug("Trying to parse party list");

        Matcher partyListAllMatcher = StyledTextUtils.unwrap(styledText).getMatcher(PARTY_LIST_ALL);
        if (!partyListAllMatcher.matches()) {
            SequoiaMod.debug("Failed to parse party list");
            return Collections.emptyList();
        } else {
            String[] partyMembers = StyledText.fromString(partyListAllMatcher.group(1))
                    .getStringWithoutFormatting()
                    .split("(?:,(?: and)? )");
            List<String> partyMemberList = Lists.newArrayList();
            Collections.addAll(partyMemberList, partyMembers);

            SequoiaMod.debug("Found party members in party list: " + partyMemberList);
            return partyMemberList;
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled()
                && SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType()
                        != PartyRaidCompletionsDisplayType.DISABLED;
    }
}
