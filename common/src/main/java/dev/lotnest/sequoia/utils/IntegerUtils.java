/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.utils;

public final class IntegerUtils {
    private IntegerUtils() {}

    public static int convertToInt(String input) {
        input = input.replace(",", "");
        double v = Double.parseDouble(input.substring(0, input.length() - 1));

        if (input.endsWith("k") || input.endsWith("K")) {
            return (int) (v * 1000);
        } else if (input.endsWith("m") || input.endsWith("M")) {
            return (int) (v * 1000000);
        } else {
            double value = Double.parseDouble(input);
            return (int) value;
        }
    }
}
