package dev.lotnest.sequoia.utils;

public final class URLUtils {
    private URLUtils() {}

    public static String sanitize(String url) {
        return url.replace(" ", "%20");
    }
}
