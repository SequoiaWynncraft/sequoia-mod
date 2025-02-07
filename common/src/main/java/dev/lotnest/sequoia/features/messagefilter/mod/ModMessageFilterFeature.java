/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.messagefilter.mod;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.features.messagefilter.MessageFilterDecisionType;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class ModMessageFilterFeature extends Feature {
    private static final Map<Pattern, String> PATTERN_ACTIONS = Maps.newHashMap();

    static {
        initializePatternActions();
    }

    private static void initializePatternActions() {
        addPatternsToMap(ModMessageFilterPatterns.WYNNTILS_CONNECTION, "WYNNTILS_CONNECTION");
        addPatternsToMap(ModMessageFilterPatterns.FUY_GG_CONNECTION, "FUY_GG_CONNECTION");
    }

    private static void addPatternsToMap(Pattern[] patterns, String category) {
        for (Pattern pattern : patterns) {
            PATTERN_ACTIONS.put(pattern, category);
        }
    }

    private static MessageFilterDecisionType getUserDecisionType(String category) {
        return switch (category) {
            case "WYNNTILS_CONNECTION" -> SequoiaMod.CONFIG.modMessageFilterFeature
                    .wynntilsConnectionMessagesFilterDecisionType();
            case "FUY_GG_CONNECTION" -> SequoiaMod.CONFIG.modMessageFilterFeature
                    .fuyggConnectionMessagesFilterDecisionType();
            default -> MessageFilterDecisionType.KEEP;
        };
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        for (Map.Entry<Pattern, String> patternEntry : PATTERN_ACTIONS.entrySet()) {
            Pattern pattern = patternEntry.getKey();
            String category = patternEntry.getValue();
            MessageFilterDecisionType messageFilterDecisionType = getUserDecisionType(category);
            String unformattedMessage =
                    WynnUtils.getUnformattedString(event.getOriginalStyledText().getStringWithoutFormatting());
            Matcher matcher = pattern.matcher(unformattedMessage);

            if (matcher.find()) {
                SequoiaMod.debug("[" + ModMessageFilterFeature.class.getSimpleName() + "] Pattern in category '"
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
        return SequoiaMod.CONFIG.modMessageFilterFeature.enabled();
    }
}
