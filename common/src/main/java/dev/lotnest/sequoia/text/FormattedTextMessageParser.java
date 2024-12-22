package dev.lotnest.sequoia.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class FormattedTextMessageParser {
    private static final Pattern COLOUR_TAG_PATTERN = Pattern.compile(
            "<FormattedTextMessage(?:\\s+colour=\"(#[0-9A-Fa-f]{6}|0x[0-9A-Fa-f]{6})\")?(?:\\s+bold=\"(.*?)\")?(?:\\s+italic=\"(.*?)\")?(?:\\s+underline=\"(.*?)\")?(?:\\s+strikethrough=\"(.*?)\")?(?:\\s+obfuscated=\"(.*?)\")?(?:\\s+reset=\"(true)\")?\\s*>(.*?)</FormattedTextMessage>");
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\\\\n");

    private FormattedTextMessageParser() {}

    public static MutableComponent parseString(String input) {
        MutableComponent result = Component.empty();
        String[] lines = NEW_LINE_PATTERN.split(input);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher colourTagMatcher = COLOUR_TAG_PATTERN.matcher(line);
            int lastMatchEnd = 0;

            while (colourTagMatcher.find()) {
                if (colourTagMatcher.start() > lastMatchEnd) {
                    String textBefore = line.substring(lastMatchEnd, colourTagMatcher.start());
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

            if (lastMatchEnd < line.length()) {
                String textAfter = line.substring(lastMatchEnd);
                result = result.append(Component.literal(textAfter));
            }

            if (i < lines.length - 1) {
                result = result.append(Component.literal("\n"));
            }
        }

        return result;
    }
}
