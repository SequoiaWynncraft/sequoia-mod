/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.utils;

import java.time.Duration;
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
        long totalSeconds =
                (System.currentTimeMillis() - parseTimestamp(timestamp).toEpochMilli()) / 1000;
        return toPrettyTime(totalSeconds);
    }

    public static String toPrettyTime(long totalSeconds) {
        Duration duration = Duration.ofSeconds(totalSeconds);

        long years = duration.toDays() / 365;
        duration = duration.minusDays(years * 365);

        long months = duration.toDays() / 30;
        duration = duration.minusDays(months * 30);

        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);

        long seconds = duration.getSeconds();

        return (years > 0 ? years + "y " : "") + (months > 0 ? months + "mo " : "")
                + (days > 0 ? days + "d " : "")
                + (hours > 0 ? hours + "h " : "")
                + (minutes > 0 ? minutes + "m " : "")
                + seconds
                + "s";
    }

    public static String wsTimestamp() {
        return Instant.ofEpochMilli(System.currentTimeMillis())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ROOT));
    }
}
