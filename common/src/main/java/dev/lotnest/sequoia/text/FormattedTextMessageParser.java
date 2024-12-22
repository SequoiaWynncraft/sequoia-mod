package dev.lotnest.sequoia.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class FormattedTextMessageParser {
    private static final Pattern COLOUR_TAG_PATTERN = Pattern.compile(
            "<FormattedTextMessage(?: colour=\"(.*?)\" bold=\"(.*?)\" italic=\"(.*?)\" underline=\"(.*?)\" strikethrough=\"(.*?)\" obfuscated=\"(.*?)\")?(?: reset=\"(true)\")?>((?:.|\n)*?)</FormattedTextMessage>");

    private FormattedTextMessageParser() {}

    public static MutableComponent buildFromString(String input) {
        MutableComponent result = Component.empty();
        int lastMatchEnd = 0;
        Matcher colourTagMatcher = COLOUR_TAG_PATTERN.matcher(input);

        while (colourTagMatcher.find()) {
            if (colourTagMatcher.start() > lastMatchEnd) {
                String textBefore = input.substring(lastMatchEnd, colourTagMatcher.start());
                result = result.append(Component.literal(textBefore));
            }

            boolean isReset = "true".equals(colourTagMatcher.group(7));
            if (isReset) {
                result = result.append(
                        Component.literal(colourTagMatcher.group(8)).withStyle(ChatFormatting.RESET));
                lastMatchEnd = colourTagMatcher.end();
                continue;
            }

            String rgbValue = colourTagMatcher.group(1);
            boolean isBold = Boolean.parseBoolean(colourTagMatcher.group(2));
            boolean isItalic = Boolean.parseBoolean(colourTagMatcher.group(3));
            boolean isUnderline = Boolean.parseBoolean(colourTagMatcher.group(4));
            boolean isStrikethrough = Boolean.parseBoolean(colourTagMatcher.group(5));
            boolean isObfuscated = Boolean.parseBoolean(colourTagMatcher.group(6));
            String innerText = colourTagMatcher.group(8);

            int color;
            try {
                color = rgbValue != null ? Integer.decode(rgbValue) : 0xFFFFFF;
            } catch (NumberFormatException ignored) {
                color = 0xFFFFFF;
            }

            int finalColor = color;
            result = result.append(Component.literal(innerText).withStyle(style -> style.withColor(finalColor)
                    .withBold(isBold)
                    .withItalic(isItalic)
                    .withUnderlined(isUnderline)
                    .withStrikethrough(isStrikethrough)
                    .withObfuscated(isObfuscated)));

            lastMatchEnd = colourTagMatcher.end();
        }

        if (lastMatchEnd < input.length()) {
            String textAfter = input.substring(lastMatchEnd);
            result = result.append(Component.literal(textAfter));
        }

        return result;
    }
}
