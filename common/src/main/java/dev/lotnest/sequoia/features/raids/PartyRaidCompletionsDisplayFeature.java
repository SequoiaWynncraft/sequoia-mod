/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.raids;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Feature;
import dev.lotnest.sequoia.core.components.Services;
import dev.lotnest.sequoia.core.events.PartyPlayerJoinedEvent;
import dev.lotnest.sequoia.mc.MinecraftUtils;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class PartyRaidCompletionsDisplayFeature extends Feature {
    private static final Pattern PARTY_PLAYER_JOINED_PATTERN =
            Pattern.compile("(.+) has joined your party, say hello!");

    private final Cache<String, Long> cachedPartyMembers =
            CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build();

    public enum PartyRaidCompletionsDisplayType {
        MANUAL,
        AUTOMATIC,
        DISABLED
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPartyPlayerJoinPre(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        String unformattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());
        Matcher partyPlayerJoinedMatcher = PARTY_PLAYER_JOINED_PATTERN.matcher(unformattedMessage);

        if (!partyPlayerJoinedMatcher.matches()) {
            return;
        }

        String playerName = MinecraftUtils.cleanUsername(partyPlayerJoinedMatcher.group(1));
        if (StringUtils.isBlank(playerName)) {
            return;
        }

        if (playerName.equals(McUtils.playerName())) {
            Models.Party.requestData();
            return;
        }

        WynntilsMod.postEvent(new PartyPlayerJoinedEvent(playerName));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPartyPlayerJoinPost(PartyPlayerJoinedEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (cachedPartyMembers.getIfPresent(event.getPlayerName()) != null) {
            return;
        }

        switch (SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType()) {
            case MANUAL -> handleManualDisplay(event.getPlayerName());
            case AUTOMATIC -> handleAutomaticDisplay(event.getPlayerName());
            default -> throw new IllegalStateException("Unexpected value: "
                    + SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType());
        }
    }

    private void handleManualDisplay(String playerName) {
        Managers.TickScheduler.scheduleNextTick(() -> McUtils.sendMessageToClient(
                Component.translatable("sequoia.feature.partyRaidCompletionsDisplayFeature.clickToView")
                        .withStyle(style -> style.withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/seq playerRaids " + playerName)))));
    }

    private void handleAutomaticDisplay(String playerName) {
        Services.Player.getPlayer(playerName).thenAccept(playerResponse -> {
            if (playerResponse == null) {
                return;
            }

            cachedPartyMembers.put(playerName, System.currentTimeMillis());
            McUtils.sendMessageToClient(SequoiaMod.prefix(Component.translatable(
                            "sequoia.command.playerRaids.showingPlayerRaids",
                            playerResponse.getUsername(),
                            playerResponse.getGlobalData().getRaids().getTotal())
                    .append("\n")
                    .append(playerResponse.getGlobalData().getRaids().toPrettyMessage(playerResponse.getRanking()))));
        });
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled()
                && SequoiaMod.CONFIG.raidsFeature.PartyRaidCompletionsDisplayFeature.displayType()
                        != PartyRaidCompletionsDisplayType.DISABLED;
    }
}
