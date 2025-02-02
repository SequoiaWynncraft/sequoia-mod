/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.messagefilter.mod;

import java.util.regex.Pattern;

public final class ModMessageFilterPatterns {
    public static final Pattern[] WYNNTILS_CONNECTION = {
        Pattern.compile(
                "^.*Trying to connect to Hades failed\\. Until you reconnect you will be unable to see friends or party members on your map and minimap\\. Click here to try and reconnect\\.$",
                Pattern.CASE_INSENSITIVE),
        Pattern.compile(
                "^.*Trying to connect and set up the Wynntils Account with your data has failed\\. Click here to try and reconnect\\.$",
                Pattern.CASE_INSENSITIVE),
    };

    public static final Pattern[] FUY_GG_CONNECTION = {
        Pattern.compile("^.*Connecting to Buster\\.$", Pattern.CASE_INSENSITIVE),
        Pattern.compile("^.*Logging into Buster\\.$", Pattern.CASE_INSENSITIVE),
        Pattern.compile("^.*You have been disconnected from Buster!$", Pattern.CASE_INSENSITIVE),
        Pattern.compile("^.*Error while connecting to Buster!$", Pattern.CASE_INSENSITIVE),
        Pattern.compile("^.*Successfully logged into buster!$", Pattern.CASE_INSENSITIVE)
    };

    private ModMessageFilterPatterns() {}
}
