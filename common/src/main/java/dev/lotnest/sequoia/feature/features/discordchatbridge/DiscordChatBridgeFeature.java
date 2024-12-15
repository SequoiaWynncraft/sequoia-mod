package dev.lotnest.sequoia.feature.features.discordchatbridge;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.utils.TimeUtils;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
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
    private static final Pattern GUILD_CHAT_PATTERN = Pattern.compile(
            "^[\\s\\p{C}\\p{M}\\p{So}\\p{Sk}\\p{P}\\p{Z}\\p{S}\\p{L}\\p{N}]*?([^:]+):\\s*((?:.+\\n?)*)$",
            Pattern.MULTILINE);

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("(.*?)'s? real username is (.*)", Pattern.MULTILINE);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (event.getStyledText() == null || event.getStyledText().isBlank()) {
            return;
        }

        MutableComponent messageComponent = event.getStyledText().getComponent();
        String messageStringWithoutFormatting = event.getStyledText().getStringWithoutFormatting();

        SequoiaMod.debug("[CHAT] " + event.getStyledText().getString());

        if (!SequoiaMod.CONFIG.discordChatBridgeFeature.enabled()) {
            return;
        }

        if (!SequoiaMod.CONFIG.discordChatBridgeFeature.sendInGameGuildChatMessagesToDiscord()) {
            return;
        }

        if (!event.getStyledText().contains("§b") && !event.getStyledText().contains("§3")) {
            return;
        }

        if (!containsUnicode(messageStringWithoutFormatting) || messageStringWithoutFormatting.startsWith("[Event]")) {
            return;
        }

        if (SequoiaWebSocketClient.getInstance().isClosed()) {
            try {
                SequoiaWebSocketClient.getInstance().reconnect();
            } catch (Exception exception) {
                SequoiaMod.error("Failed to connect to WebSocket server", exception);
                return;
            }
        }

        Matcher guildChatMatcher = GUILD_CHAT_PATTERN.matcher(messageStringWithoutFormatting);
        Map<String, List<String>> nameMap = Maps.newHashMap();
        String nickname;
        String username = null;

        createRealNameMap(messageComponent, nameMap);

        try {
            if (guildChatMatcher.matches()) {
                nickname = guildChatMatcher
                        .group(1)
                        .replaceAll("[^\\x20-\\x7E]", "")
                        .replace("\n", " ")
                        .trim();
                String message = guildChatMatcher
                        .group(2)
                        .replaceAll("[^\\x20-\\x7E]", "")
                        .replace("\n", " ")
                        .trim();

                if (nickname != null && nameMap.containsKey(nickname)) {
                    username = nameMap.get(nickname).get(0);
                }

                if (username == null) {
                    SequoiaMod.debug("No username found for nickname: " + nickname);
                    return;
                }

                GChatMessageWSMessage gChatMessageWSMessage = new GChatMessageWSMessage(new GChatMessageWSMessage.Data(
                        username, nickname, message, TimeUtils.wsTimestamp(), McUtils.playerName()));

                SequoiaWebSocketClient.getInstance().sendAsJson(gChatMessageWSMessage);
                SequoiaMod.debug("Sent guild chat message to Discord: " + gChatMessageWSMessage);
            }
        } catch (Exception ignored) {
            return;
        }
    }

    private static boolean containsUnicode(String message) {
        return message.matches(".*[\\P{ASCII}].*");
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
                    SequoiaMod.debug("No match for hover text: " + hoverText.getString());
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
            SequoiaMod.debug("Hover text found: " + hoverString);
            return hoverString.contains("real username");
        }
        return false;
    }
}
