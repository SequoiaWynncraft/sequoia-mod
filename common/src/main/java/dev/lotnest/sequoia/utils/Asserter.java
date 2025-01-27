/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.utils;

import java.util.regex.Pattern;

public final class Asserter {
    private Asserter() {}

    public static void assertMatches(Pattern pattern, String input) {
        assert pattern.matcher(input).find();
    }
}
