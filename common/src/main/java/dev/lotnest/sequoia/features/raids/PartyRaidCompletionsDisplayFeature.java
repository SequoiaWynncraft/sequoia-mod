/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.raids;

import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.TitleSetTextEvent;
import com.wynntils.models.raid.event.RaidEndedEvent;
import com.wynntils.models.raid.type.RaidKind;
import com.wynntils.models.raid.type.RaidRoomType;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Services;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class PartyRaidCompletionsDisplayFeature extends Feature {
    private boolean shownRaidCompletionsForCurrentParty = false;

    public enum PartyRaidCompletionsDisplayType {
        MANUAL,
        AUTOMATIC,
        DISABLED
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onRaidIntro(TitleSetTextEvent event) {
        if (shownRaidCompletionsForCurrentParty) return;

        Managers.TickScheduler.scheduleNextTick(() -> {
            Component component = event.getComponent();
            StyledText styledText = StyledText.fromComponent(component);
            RaidKind raidKind = RaidKind.fromTitle(styledText);

            if (raidKind != null && Models.Raid.getCurrentRoom() == RaidRoomType.INTRO) {
                Managers.TickScheduler.scheduleNextTick(() -> {
                    getPartyMembers().forEach(partyMember -> {
                        switch (SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType()) {
                            case MANUAL -> handleManualDisplay(partyMember);
                            case AUTOMATIC -> handleAutomaticDisplay(partyMember);
                            default -> throw new IllegalStateException("Unexpected value: "
                                    + SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType());
                        }
                    });
                    shownRaidCompletionsForCurrentParty = true;
                });
            }
        });
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

    private List<String> getPartyMembers() {
        Models.Party.requestData();
        return Stream.concat(
                        Stream.of(Models.Party.getPartyLeader().orElse(null)), Models.Party.getPartyMembers().stream())
                .filter(StringUtils::isNotBlank)
                .filter(username -> !McUtils.playerName().equals(username))
                .toList();
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled()
                && SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType()
                        != PartyRaidCompletionsDisplayType.DISABLED;
    }
}
