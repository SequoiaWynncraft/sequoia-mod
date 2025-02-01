/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.guildraidtracker;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.type.MessageType;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Feature;
import dev.lotnest.sequoia.core.websocket.WSMessage;
import dev.lotnest.sequoia.core.websocket.messages.GuildRaidWSMessage;
import dev.lotnest.sequoia.features.messagefilter.guild.GuildMessageFilterPatterns;
import dev.lotnest.sequoia.utils.LongUtils;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class GuildRaidTrackerFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuildRaidCompletion(ChatMessageReceivedEvent event) {
        if (event.getMessageType() != MessageType.FOREGROUND) {
            return;
        }

        if (SequoiaMod.getWebSocketFeature() == null
                || !SequoiaMod.getWebSocketFeature().isEnabled()) {
            return;
        }

        if (SequoiaMod.getWebSocketFeature().isAuthenticating()) {
            return;
        }

        if (!isEnabled()) {
            return;
        }

        if (event.getStyledText() == null || event.getStyledText().isBlank()) {
            return;
        }

        Component message = event.getStyledText().getComponent();
        String unformattedMessage = WynnUtils.getUnformattedString(message.getString());
        Map<String, List<String>> nameMap = Maps.newHashMap();

        Matcher guildRaidCompletionMatcher = GuildMessageFilterPatterns.RAID[0].matcher(unformattedMessage);
        if (!guildRaidCompletionMatcher.matches()) {
            return;
        }

        createUsernameMap(message, nameMap);

        String player1 = extractUsername(guildRaidCompletionMatcher.group("player1"), nameMap);
        String player2 = extractUsername(guildRaidCompletionMatcher.group("player2"), nameMap);
        String player3 = extractUsername(guildRaidCompletionMatcher.group("player3"), nameMap);
        String player4 = extractUsername(guildRaidCompletionMatcher.group("player4"), nameMap);

        String raidString = guildRaidCompletionMatcher.group("raid");
        RaidType raidType = RaidType.getRaidType(raidString);
        String aspects = guildRaidCompletionMatcher.group("aspects");
        String emeralds = guildRaidCompletionMatcher.group("emeralds");
        String xp = guildRaidCompletionMatcher.group("xp").replaceAll("[^0-9km]", "");
        String sr = guildRaidCompletionMatcher.group("sr") != null ? guildRaidCompletionMatcher.group("sr") : "0";
        UUID reporterID = Minecraft.getInstance().player.getUUID();

        if (raidType == null) {
            SequoiaMod.error("Failed to parse RaidType: " + raidString);
            McUtils.sendMessageToClient(
                    Component.literal("Failed to report Guild Raid completion, unknown RaidType: " + raidString)
                            .withStyle(ChatFormatting.RED));
            return;
        }

        sendGuildRaidCompletionReport(new GuildRaid(
                raidType,
                List.of(player1, player2, player3, player4),
                reporterID,
                Long.parseLong(aspects),
                Long.parseLong(emeralds),
                LongUtils.convertToLong(xp),
                StringUtils.isNotBlank(sr) ? Integer.parseInt(sr) : 0));
    }

    private static String extractUsername(String nickname, Map<String, List<String>> nameMap) {
        if (nameMap.containsKey(nickname)) {
            return nameMap.get(nickname).removeLast();
        }
        return nickname;
    }

    private static void sendGuildRaidCompletionReport(GuildRaid guildRaid) {
        if (guildRaid == null) {
            return;
        }

        try {
            WSMessage guildRaidWSMessage = new GuildRaidWSMessage(guildRaid);
            String payload = SequoiaMod.getWebSocketFeature().sendAsJson(guildRaidWSMessage);
            if (StringUtils.isNotBlank(payload)) {
                SequoiaMod.debug("Sending Guild Raid completion: " + payload);
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to send Guild Raid completion report", exception);
            McUtils.sendMessageToClient(
                    Component.literal("Failed to report Guild Raid completion, check the logs for more info.")
                            .withStyle(ChatFormatting.RED));
        }
    }

    private static void createUsernameMap(Component message, Map<String, List<String>> nameMap) {
        if (!messageHasNickHoverDeep(message)) {
            return;
        }

        if (!message.getSiblings().isEmpty()) {
            for (Component siblingMessage : message.getSiblings()) {
                if (messageHasNickHoverDeep(siblingMessage)) {
                    createUsernameMap(siblingMessage, nameMap);
                    tryToAddUsername(siblingMessage, nameMap);
                }
            }
        }
    }

    private static void tryToAddUsername(Component message, Map<String, List<String>> nameMap) {
        if (messageHasNickHover(message)) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            if (hover == null) {
                return;
            }
            if (hover.getValue(hover.getAction()) instanceof Component hoverText) {
                Matcher matcher = Pattern.compile("(.*?)'s? real username is (.*)", Pattern.MULTILINE)
                        .matcher(hoverText.getString());

                if (!matcher.matches()) {
                    return;
                }

                String username = matcher.group(2);
                String nickname = matcher.group(1);

                nameMap.computeIfAbsent(nickname, k -> new ArrayList<>()).add(username);
            }
        }
    }

    private static boolean messageHasNickHoverDeep(Component message) {
        if (!message.getSiblings().isEmpty()) {
            for (Component messageSibling : message.getSiblings()) {
                if (messageHasNickHoverDeep(messageSibling)) {
                    return true;
                }
            }
        }
        return messageHasNickHover(message);
    }

    private static boolean messageHasNickHover(Component message) {
        HoverEvent hover = message.getStyle().getHoverEvent();
        if (hover != null && hover.getValue(hover.getAction()) instanceof Component hoverText) {
            return hoverText.getString().contains("real username");
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.guildRaidTrackerFeature.enabled();
    }
}
