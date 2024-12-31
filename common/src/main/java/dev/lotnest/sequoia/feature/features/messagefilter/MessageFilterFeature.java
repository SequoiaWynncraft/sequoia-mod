package dev.lotnest.sequoia.feature.features.messagefilter;

import com.google.common.collect.Maps;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Pattern;

public class MessageFilterFeature extends Feature {
    private static final Map<Pattern, String> PATTERN_ACTIONS = Maps.newHashMap();

    static {
        try {
            Field[] fields = MessageFilterPatterns.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(Pattern.class)) {
                    field.setAccessible(true);
                    Pattern pattern = (Pattern) field.get(null);
                    PATTERN_ACTIONS.put(pattern, field.getName());
                }
            }
        } catch (IllegalAccessException exception) {
            SequoiaMod.error("Failed to initialize " + MessageFilterFeature.class.getSimpleName(), exception);
        }
    }

    //    @SubscribeEvent(priority = EventPriority.HIGHEST)
    //    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
    //        if (!SequoiaMod.CONFIG.messageFilterFeature.enabled()) {
    //            return;
    //        }
    //
    //        String unformattedMessage =
    //                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());
    //
    //        for (Map.Entry<Pattern, String> patternEntry : PATTERN_ACTIONS.entrySet()) {
    //            Pattern pattern = patternEntry.getKey();
    //            String patternName = patternEntry.getValue();
    //
    //            Matcher matcher = pattern.matcher(unformattedMessage);
    //            if (matcher.matches()) {
    //                SequoiaMod.debug("[" + MessageFilterFeature.class.getSimpleName() + "] " + patternName + "
    // matched");
    //            }
    //        }
    //    }
}
