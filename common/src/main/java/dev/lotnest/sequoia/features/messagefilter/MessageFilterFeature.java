/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.messagefilter;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.type.MessageType;
import com.wynntils.handlers.chat.type.RecipientType;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Feature;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class MessageFilterFeature extends Feature {
    private static final Map<Pattern, String> PATTERN_ACTIONS = Maps.newHashMap();

    static {
        initializePatternActions();
    }

    private static void initializePatternActions() {
        addPatternsToMap(MessageFilterPatterns.EVENT, "EVENT");
        addPatternsToMap(MessageFilterPatterns.PARTY_FINDER, "PARTY_FINDER");
        addPatternsToMap(MessageFilterPatterns.CRATE, "CRATE");
        addPatternsToMap(MessageFilterPatterns.PET, "PET");
    }

    private static void addPatternsToMap(Pattern[] patterns, String category) {
        for (Pattern pattern : patterns) {
            PATTERN_ACTIONS.put(pattern, category);
        }
    }

    private static MessageFilterDecisionType getUserDecisionType(String category) {
        return switch (category) {
            case "EVENT" -> SequoiaMod.CONFIG.messageFilterFeature.eventMessagesFilterDecisionType();
            case "PARTY_FINDER" -> SequoiaMod.CONFIG.messageFilterFeature.partyFinderMessagesFilterDecisionType();
            case "CRATE" -> SequoiaMod.CONFIG.messageFilterFeature.crateMessagesFilterDecisionType();
            case "PET" -> SequoiaMod.CONFIG.messageFilterFeature.petMessagesFilterDecisionType();
            default -> MessageFilterDecisionType.KEEP;
        };
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
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
                if (StringUtils.equals(category, "PET")
                        && !RecipientType.PETS.matchPattern(event.getStyledText(), MessageType.FOREGROUND)
                        && !RecipientType.PETS.matchPattern(event.getStyledText(), MessageType.BACKGROUND)) {
                    continue;
                }

                SequoiaMod.debug("[" + MessageFilterFeature.class.getSimpleName() + "] Pattern in category '"
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
        return SequoiaMod.CONFIG.messageFilterFeature.enabled();
    }
}
