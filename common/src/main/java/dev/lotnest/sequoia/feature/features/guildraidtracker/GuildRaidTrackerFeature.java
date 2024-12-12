package dev.lotnest.sequoia.feature.features.guildraidtracker;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.utils.IntegerUtils;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.wynn.WynnUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildRaidTrackerFeature extends Feature {
    private static final Pattern GUILD_RAID_COMPLETION_PATTERN = Pattern.compile(
            "([A-Za-z0-9_ ]+?), ([A-Za-z0-9_ ]+?), ([A-Za-z0-9_ ]+?), and "
                    + "([A-Za-z0-9_ ]+?) finished (.+?) and claimed (\\d+)x Aspects, (\\d+)x Emeralds, .(.+?m)"
                    + " Guild Experience, and \\+(\\d+) Seasonal Rating",
            Pattern.MULTILINE);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuildRaidCompletion(ChatMessageReceivedEvent event) {
        if (event.getStyledText() == null) {
            return;
        }

        if (event.getStyledText().isBlank()) {
            return;
        }

        Component message = event.getStyledText().getComponent();
        String unformattedMessage = WynnUtils.getUnformattedString(message.getString());
        Matcher guildRaidCompletionMatcher = GUILD_RAID_COMPLETION_PATTERN.matcher(unformattedMessage);
        Map<String, List<String>> nameMap = Maps.newHashMap();

        createRealNameMap(message, nameMap);
        if (!guildRaidCompletionMatcher.matches()) {
            return;
        }

        String player1 = guildRaidCompletionMatcher.group(1);
        if (nameMap.containsKey(player1)) {
            player1 = nameMap.get(player1).removeLast();
        }

        String player2 = guildRaidCompletionMatcher.group(2);
        if (nameMap.containsKey(player2)) {
            player2 = nameMap.get(player2).removeLast();
        }

        String player3 = guildRaidCompletionMatcher.group(3);
        if (nameMap.containsKey(player3)) {
            player3 = nameMap.get(player3).removeLast();
        }

        String player4 = guildRaidCompletionMatcher.group(4);
        if (nameMap.containsKey(player4)) {
            player4 = nameMap.get(player4).removeLast();
        }

        String raidString = guildRaidCompletionMatcher.group(5);
        String aspects = guildRaidCompletionMatcher.group(6);
        String emeralds = guildRaidCompletionMatcher.group(7);
        String xp = guildRaidCompletionMatcher.group(8);
        String sr = guildRaidCompletionMatcher.group(9);
        RaidType raidType = RaidType.getRaidType(raidString);
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

    private void sendGuildRaidCompletionReport(GuildRaid guildRaid) {
        try {
            WSMessage guildRaidWSMessage = new GuildRaidWSMessage(guildRaid);
            String payload = SequoiaWebSocketClient.getInstance().sendAsJson(guildRaidWSMessage);
            SequoiaMod.debug("Sent Guild Raid completion: " + payload);
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

                if (nameMap.containsKey(nickname)) {
                    nameMap.get(nickname).add(realName);
                } else {
                    nameMap.put(nickname, new ArrayList<>(Collections.singletonList(realName)));
                }
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
