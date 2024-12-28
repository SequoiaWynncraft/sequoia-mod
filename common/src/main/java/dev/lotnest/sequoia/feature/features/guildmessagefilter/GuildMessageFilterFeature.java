package dev.lotnest.sequoia.feature.features.guildmessagefilter;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.type.MessageType;
import com.wynntils.handlers.chat.type.RecipientType;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.wynn.WynnUtils;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class GuildMessageFilterFeature extends Feature {
    private static final Map<Pattern, String> PATTERN_ACTIONS = Maps.newHashMap();

    static {
        try {
            Field[] fields = GuildMessageFilterPatterns.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(Pattern.class)) {
                    field.setAccessible(true);
                    Pattern pattern = (Pattern) field.get(null);
                    PATTERN_ACTIONS.put(pattern, field.getName());
                }
            }
        } catch (IllegalAccessException exception) {
            SequoiaMod.error("Failed to initialize " + GuildMessageFilterFeature.class.getSimpleName(), exception);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!RecipientType.GUILD.matchPattern(event.getStyledText(), MessageType.FOREGROUND)
                && !RecipientType.GUILD.matchPattern(event.getStyledText(), MessageType.BACKGROUND)) {
            return;
        }

        String unformattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        for (Map.Entry<Pattern, String> patternEntry : PATTERN_ACTIONS.entrySet()) {
            Pattern pattern = patternEntry.getKey();
            String patternName = patternEntry.getValue();

            Matcher matcher = pattern.matcher(unformattedMessage);
            if (matcher.matches()) {
                SequoiaMod.debug(
                        "[" + GuildMessageFilterFeature.class.getSimpleName() + "] " + patternName + " matched");
            }
        }
    }
}
