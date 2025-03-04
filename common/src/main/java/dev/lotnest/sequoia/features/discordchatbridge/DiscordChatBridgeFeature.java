/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.discordchatbridge;

import com.google.common.collect.Maps;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.type.MessageType;
import com.wynntils.handlers.chat.type.RecipientType;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.core.ws.message.ws.discordchatbridge.GChatMessageWSMessage;
import dev.lotnest.sequoia.utils.TimeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class DiscordChatBridgeFeature extends Feature {
    protected static final Pattern GUILD_CHAT_PATTERN = Pattern.compile(
            "^[\\s\\p{C}\\p{M}\\p{So}\\p{Sk}\\p{P}\\p{Z}\\p{S}\\p{L}\\p{N}§[0-9a-fk-or<]*]*?([^:]+):\\s*(.+)$",
            Pattern.MULTILINE);
    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("(.*?)'s? real username is (.*)", Pattern.MULTILINE);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (event.getMessageType() != MessageType.FOREGROUND) {
            return;
        }

        if (!RecipientType.GUILD.matchPattern(event.getOriginalStyledText(), MessageType.FOREGROUND)) {
            return;
        }

        StyledText messageTextWithoutNewLines = event.getOriginalStyledText()
                .replaceAll("\n", "")
                .replaceAll("\uDAFF\uDFFC\uE001\uDB00\uDC06\\s+", "")
                .replaceAll("\\s{2,}", " ");
        MutableComponent messageComponent = messageTextWithoutNewLines.getComponent();

        if (messageTextWithoutNewLines.isBlank()) {
            return;
        }

        SequoiaMod.debug("[CHAT] " + messageTextWithoutNewLines.getString());

        if (SequoiaMod.getWebSocketFeature() == null
                || !SequoiaMod.getWebSocketFeature().isEnabled()) {
            return;
        }

        if (!SequoiaMod.getWebSocketFeature().isAuthenticated()) {
            return;
        }

        if (!SequoiaMod.CONFIG.discordChatBridgeFeature.enabled()) {
            return;
        }

        if (!SequoiaMod.CONFIG.discordChatBridgeFeature.sendInGameGuildChatMessagesToDiscord()) {
            return;
        }

        String messageStringWithoutFormatting = messageTextWithoutNewLines.getStringWithoutFormatting();
        Matcher guildChatMatcher = GUILD_CHAT_PATTERN.matcher(messageStringWithoutFormatting);
        Map<String, List<String>> nameMap = Maps.newHashMap();
        String nickname;
        String username = null;

        createRealNameMap(messageComponent, nameMap);

        try {
            if (guildChatMatcher.matches()) {
                nickname = guildChatMatcher
                        .group(1)
                        .replaceAll("[^\\p{Print}]", "")
                        .trim();
                String message = guildChatMatcher
                        .group(2)
                        .replaceAll("[^\\p{Print}]", "")
                        .trim();

                if (nickname != null && nameMap.containsKey(nickname)) {
                    username = nameMap.get(nickname).getFirst();
                }

                if (username == null) {
                    username = nickname;
                    nickname = null;
                }

                GChatMessageWSMessage gChatMessageWSMessage = new GChatMessageWSMessage(new GChatMessageWSMessage.Data(
                        username, nickname, message, TimeUtils.wsTimestamp(), McUtils.playerName()));
                SequoiaMod.getWebSocketFeature().sendMessage(gChatMessageWSMessage);
                SequoiaMod.debug("Sending guild chat message to Discord: " + gChatMessageWSMessage);
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to send guild chat message to Discord", exception);
        }
    }

    private static void createRealNameMap(Component message, Map<String, List<String>> nameMap) {
        if (!messageHasNickHoverDeep(message)) {
            return;
        }

        if (!message.getSiblings().isEmpty()) {
            for (Component siblingMessage : message.getSiblings()) {
                if (messageHasNickHoverDeep(siblingMessage)) {
                    createRealNameMap(siblingMessage, nameMap);
                    tryToAddRealName(siblingMessage, nameMap);
                }
            }
        }
    }

    private static void tryToAddRealName(Component message, Map<String, List<String>> nameMap) {
        if (messageHasNickHover(message)) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            if (hover == null) {
                return;
            }
            if (hover.getValue(hover.getAction()) instanceof Component hoverText) {
                Matcher nicknameMatcher = NICKNAME_PATTERN.matcher(hoverText.getString());
                if (!nicknameMatcher.matches()) {
                    return;
                }

                String nickname = nicknameMatcher.group(1);
                String username = nicknameMatcher.group(2);

                SequoiaMod.debug("Mapped nickname '" + nickname + "' to username '" + username + "'");
                nameMap.computeIfAbsent(nickname, k -> new ArrayList<>()).add(username);
            }
        }
    }

    private static boolean messageHasNickHoverDeep(Component message) {
        boolean hasNick = false;
        if (!message.getSiblings().isEmpty()) {
            for (Component messageSibling : message.getSiblings()) {
                hasNick = hasNick || messageHasNickHoverDeep(messageSibling);
            }
        } else {
            return messageHasNickHover(message);
        }
        return hasNick;
    }

    private static boolean messageHasNickHover(Component message) {
        HoverEvent hover = message.getStyle().getHoverEvent();
        if (hover != null && hover.getValue(hover.getAction()) instanceof Component hoverText) {
            String hoverString = hoverText.getString();
            return hoverString.contains("real username");
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.discordChatBridgeFeature.enabled();
    }
}
