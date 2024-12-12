package dev.lotnest.sequoia.feature.features.discordchatbridge;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Category;
import dev.lotnest.sequoia.feature.CategoryType;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category(CategoryType.CHAT)
public class DiscordChatBridgeFeature extends Feature {
    private static final Pattern GUILD_CHAT_PATTERN =
            Pattern.compile("([^\\x00-\\x7F]+).*?([A-Za-z0-9_][A-Za-z0-9_ ]*?):\\s*(.+)", Pattern.MULTILINE);
    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("(.*?)'s? real username is (.*)", Pattern.MULTILINE);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!SequoiaMod.CONFIG.discordChatBridgeFeature.enabled()) {
            return;
        }

        if (!SequoiaMod.CONFIG.discordChatBridgeFeature.sendInGameGuildChatMessagesToDiscord()) {
            return;
        }

        if (event.getStyledText() == null || event.getStyledText().isBlank()) {
            return;
        }

        MutableComponent messageComponent = event.getStyledText().getComponent();
        String messageStringWithoutFormatting = event.getStyledText().getStringWithoutFormatting();

        SequoiaMod.debug("[CHAT] " + messageStringWithoutFormatting);

        if (SequoiaWebSocketClient.getInstance().isClosed()) {
            try {
                SequoiaWebSocketClient.getInstance().connectBlocking();
            } catch (Exception exception) {
                SequoiaMod.error("Failed to connect to WebSocket server", exception);
                return;
            }
        }

        Matcher guildChatMatcher = GUILD_CHAT_PATTERN.matcher(messageStringWithoutFormatting);
        Map<String, List<String>> nameMap = Maps.newHashMap();
        String nickname = null;
        String username = null;

        createRealNameMap(messageComponent, nameMap);

        try {
            if (guildChatMatcher.matches()) {
                nickname = guildChatMatcher.group(2);
                username = null;
            }

            if (nickname != null && nameMap.containsKey(nickname)) {
                username = nameMap.get(nickname).getFirst();
            }

            if (username == null) {
                username = nickname;
                nickname = null;
            }

            if (username == null && nickname == null) {
                return;
            }
        } catch (Exception ignored) {
            return;
        }

        GChatMessageWSMessage gChatMessageWSMessage = new GChatMessageWSMessage(new GChatMessageWSMessage.Data(
                username,
                nickname,
                messageStringWithoutFormatting,
                Instant.ofEpochMilli(System.currentTimeMillis())
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ROOT)),
                McUtils.playerName()));

        SequoiaWebSocketClient.getInstance().sendAsJson(gChatMessageWSMessage);
        SequoiaMod.debug("Sent guild chat message to Discord: " + gChatMessageWSMessage);
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
            return hoverText.getString().contains("real username");
        }
        return false;
    }
}
