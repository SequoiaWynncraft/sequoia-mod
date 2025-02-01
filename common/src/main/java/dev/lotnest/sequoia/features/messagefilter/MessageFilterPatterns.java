/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.messagefilter;

import java.util.regex.Pattern;

public final class MessageFilterPatterns {
    public static final Pattern[] EVENT = {Pattern.compile("^\\[Event] .+$")};

    public static final Pattern[] PARTY_FINDER = {
        Pattern.compile(
                "^Party Finder:\\s+Hey [\\w ]+, over here! Join the [a-zA-Z' ]+ queue and match up with \\d{1,2} other players?!$"),
    };

    public static final Pattern[] CRATE = {
        Pattern.compile("^[\\w ]+ has gotten a [\\w ]+ from their crate\\. Buy your own at wynncraft\\.com/store$")
    };

    public static final Pattern[] PET = {Pattern.compile("^(.*): (.*)$")};

    private MessageFilterPatterns() {}
}
