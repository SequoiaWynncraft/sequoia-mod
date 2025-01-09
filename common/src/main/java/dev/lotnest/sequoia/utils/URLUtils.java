/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.utils;

public final class URLUtils {
    private URLUtils() {}

    public static String sanitize(String url) {
        return url.replace(" ", "%20");
    }
}
