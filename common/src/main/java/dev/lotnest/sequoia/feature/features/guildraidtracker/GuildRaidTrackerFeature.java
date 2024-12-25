package dev.lotnest.sequoia.feature.features.guildraidtracker;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.utils.IntegerUtils;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.wynn.WynnUtils;
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
    private static final Pattern GUILD_RAID_COMPLETION_PATTERN = Pattern.compile(
            "([A-Za-z0-9_ ]+?), ([A-Za-z0-9_ ]+?), ([A-Za-z0-9_ ]+?), and "
                    + "([A-Za-z0-9_ ]+?) finished (.+?) and claimed (\\d+)x Aspects, (\\d+)x Emeralds, (.+?m)"
                    + " Guild Experience(?:, and \\+(\\d+) Seasonal Rating)?",
            Pattern.MULTILINE);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuildRaidCompletion(ChatMessageReceivedEvent event) {
        if (event.getStyledText() == null || event.getStyledText().isBlank()) {
            return;
        }

        if (SequoiaMod.getWebSocketClient() == null
                || SequoiaMod.getWebSocketClient().isAuthenticating()) {
            return;
        }

        Component message = event.getStyledText().getComponent();
        String unformattedMessage = WynnUtils.getUnformattedString(message.getString());
        Map<String, List<String>> nameMap = Maps.newHashMap();

        Matcher guildRaidCompletionMatcher = GUILD_RAID_COMPLETION_PATTERN.matcher(unformattedMessage);
        if (!guildRaidCompletionMatcher.matches()) {
            return;
        }

        createRealNameMap(message, nameMap);

        String player1 = extractRealName(guildRaidCompletionMatcher.group(1), nameMap);
        String player2 = extractRealName(guildRaidCompletionMatcher.group(2), nameMap);
        String player3 = extractRealName(guildRaidCompletionMatcher.group(3), nameMap);
        String player4 = extractRealName(guildRaidCompletionMatcher.group(4), nameMap);

        String raidString = guildRaidCompletionMatcher.group(5);
        RaidType raidType = RaidType.getRaidType(raidString);
        String aspects = guildRaidCompletionMatcher.group(6);
        String emeralds = guildRaidCompletionMatcher.group(7);
        String xp = guildRaidCompletionMatcher.group(8).replaceAll("[^0-9km]", "");
        String sr = guildRaidCompletionMatcher.groupCount() >= 9 && guildRaidCompletionMatcher.group(9) != null
                ? guildRaidCompletionMatcher.group(9)
                : "0";
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
                Integer.parseInt(aspects),
                Integer.parseInt(emeralds),
                IntegerUtils.convertToInt(xp),
                Integer.parseInt(sr)));
    }

    private String extractRealName(String nickname, Map<String, List<String>> nameMap) {
        if (nameMap.containsKey(nickname)) {
            return nameMap.get(nickname).remove(nameMap.get(nickname).size() - 1);
        }
        return nickname;
    }

    private void sendGuildRaidCompletionReport(GuildRaid guildRaid) {
        if (guildRaid == null) {
            return;
        }

        try {
            WSMessage guildRaidWSMessage = new GuildRaidWSMessage(guildRaid);
            String payload = SequoiaMod.getWebSocketClient().sendAsJson(guildRaidWSMessage);
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
                Matcher matcher = Pattern.compile("(.*?)'s? real username is (.*)", Pattern.MULTILINE)
                        .matcher(hoverText.getString());

                if (!matcher.matches()) {
                    return;
                }

                String realName = matcher.group(2);
                String nickname = matcher.group(1);

                nameMap.computeIfAbsent(nickname, k -> new ArrayList<>()).add(realName);
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
}
