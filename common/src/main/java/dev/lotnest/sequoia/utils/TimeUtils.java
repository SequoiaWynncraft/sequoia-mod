package dev.lotnest.sequoia.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class TimeUtils {
    private TimeUtils() {}

    public static Instant parseTimestamp(String timestamp) {
        if (StringUtils.endsWith(timestamp, "000")) {
            return Instant.parse(timestamp.replace("000", "Z"));
        }
        return Instant.parse(timestamp);
    }

    public static String toPrettyTimeSince(String timestamp) {
        return toPrettyTime(
                (int) ((System.currentTimeMillis() - parseTimestamp(timestamp).toEpochMilli()) / 60000));
    }

    public static String toPrettyTime(int minutes) {
        return (minutes >= 1440.0 ? (int) Math.floor((minutes / 1440.0)) + "d " : "")
                + (int) (Math.floor((minutes % 1440) / 60.0)) + "h " + minutes % 60 + "m";
    }

    public static String wsTimestamp() {
        return Instant.ofEpochMilli(System.currentTimeMillis())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ROOT));
    }
}
