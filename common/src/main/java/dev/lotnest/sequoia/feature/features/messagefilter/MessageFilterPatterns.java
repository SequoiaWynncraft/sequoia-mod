package dev.lotnest.sequoia.feature.features.messagefilter;

import java.util.regex.Pattern;

public final class MessageFilterPatterns {
    // Event
    public static final Pattern EVENT = Pattern.compile("^\\[Event] .+$");

    // Party Finder
    public static final Pattern PARTY_FINDER = Pattern.compile(
            "^Party Finder: Hey [\\w ]+, over here! Join the [a-zA-Z' ]+ queue and match up with \\d{1,2} other players?!$");

    // Crate
    public static final Pattern CRATE =
            Pattern.compile("^[\\w ]+ has gotten a [\\w ]+ from their crate\\. Buy your own at wynncraft\\.com/store$");

    // Pet
    public static final Pattern PET = Pattern.compile("^[\\w ]+: \\*.*\\*$");

    private MessageFilterPatterns() {}
}
