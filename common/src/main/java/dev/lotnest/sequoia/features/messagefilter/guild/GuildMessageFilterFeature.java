/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.messagefilter.guild;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.type.MessageType;
import com.wynntils.handlers.chat.type.RecipientType;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Feature;
import dev.lotnest.sequoia.features.messagefilter.MessageFilterDecisionType;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class GuildMessageFilterFeature extends Feature {
    private static final Map<Pattern, String> PATTERN_ACTIONS = Maps.newHashMap();

    static {
        initializePatternActions();
    }

    private static void initializePatternActions() {
        addPatternsToMap(GuildMessageFilterPatterns.RAID, "RAID");
        addPatternsToMap(GuildMessageFilterPatterns.WAR, "WAR");
        addPatternsToMap(GuildMessageFilterPatterns.ECONOMY, "ECONOMY");
        addPatternsToMap(GuildMessageFilterPatterns.REWARD, "REWARD");
        addPatternsToMap(GuildMessageFilterPatterns.BANK, "BANK");
        addPatternsToMap(GuildMessageFilterPatterns.RANK, "RANK");
    }

    private static void addPatternsToMap(Pattern[] patterns, String category) {
        for (Pattern pattern : patterns) {
            PATTERN_ACTIONS.put(pattern, category);
        }
    }

    private static MessageFilterDecisionType getUserDecisionType(String category) {
        return switch (category) {
            case "RAID" -> SequoiaMod.CONFIG.guildMessageFilterFeature.raidMessagesFilterDecisionType();
            case "WAR" -> SequoiaMod.CONFIG.guildMessageFilterFeature.warMessagesFilterDecisionType();
            case "ECONOMY" -> SequoiaMod.CONFIG.guildMessageFilterFeature.economyMessagesFilterDecisionType();
            case "REWARD" -> SequoiaMod.CONFIG.guildMessageFilterFeature.rewardMessagesFilterDecisionType();
            case "BANK" -> SequoiaMod.CONFIG.guildMessageFilterFeature.bankMessagesFilterDecisionType();
            case "RANK" -> SequoiaMod.CONFIG.guildMessageFilterFeature.rankMessagesFilterDecisionType();
            default -> MessageFilterDecisionType.KEEP;
        };
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (!RecipientType.GUILD.matchPattern(event.getStyledText(), MessageType.FOREGROUND)
                && !RecipientType.GUILD.matchPattern(event.getStyledText(), MessageType.BACKGROUND)) {
            return;
        }

        String unformattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        for (Map.Entry<Pattern, String> patternEntry : PATTERN_ACTIONS.entrySet()) {
            Pattern pattern = patternEntry.getKey();
            String category = patternEntry.getValue();
            MessageFilterDecisionType messageFilterDecisionType = getUserDecisionType(category);
            Matcher matcher = pattern.matcher(unformattedMessage);

            if (matcher.matches()) {
                SequoiaMod.debug("[" + GuildMessageFilterFeature.class.getSimpleName() + "] Pattern in category '"
                        + category + "' matched: " + MessageFilterDecisionType.class.getSimpleName() + "."
                        + messageFilterDecisionType.name());

                if (messageFilterDecisionType == MessageFilterDecisionType.HIDE) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.guildMessageFilterFeature.enabled();
    }
}
