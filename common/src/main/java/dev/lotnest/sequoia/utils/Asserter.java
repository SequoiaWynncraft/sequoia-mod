package dev.lotnest.sequoia.utils;

import java.util.regex.Pattern;

public final class Asserter {
    private Asserter() {}

    public static void assertMatches(Pattern pattern, String input) {
        assert pattern.matcher(input).find();
    }
}
