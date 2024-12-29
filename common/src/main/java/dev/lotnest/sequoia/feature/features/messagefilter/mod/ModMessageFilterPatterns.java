package dev.lotnest.sequoia.feature.features.messagefilter.mod;

import java.util.regex.Pattern;

public final class ModMessageFilterPatterns {
    // Wynntils
    public static final Pattern WYNNTILS_HADES_CONNECTION_FAILED = Pattern.compile(
            "^Trying to connect to Hades failed\\. Until you reconnect you will be unable to see friends or party members on your map and minimap\\. Click here to try and reconnect\\.$");
    public static final Pattern WYNNTILS_ACCOUNT_SETUP_FAILED = Pattern.compile(
            "^Trying to connect and set up the Wynntils Account with your data has failed\\. Click here to try and reconnect\\.$");

    // fuy.gg
    public static final Pattern FUY_GG_BUSTER_CONNECTING =
            Pattern.compile("^([\\w ]+ ⋙ )?Connecting to Buster\\.$", Pattern.CASE_INSENSITIVE);
    public static final Pattern FUY_GG_BUSTER_LOGGING_IN =
            Pattern.compile("^([\\w ]+ ⋙ )?Logging into Buster\\.$", Pattern.CASE_INSENSITIVE);
    public static final Pattern FUY_GG_BUSTER_DISCONNECTED =
            Pattern.compile("^([\\w ]+ ⋙ )?You have been disconnected from Buster!$", Pattern.CASE_INSENSITIVE);
    public static final Pattern FUY_GG_BUSTER_ERROR_WHILE_CONNECTING =
            Pattern.compile("^([\\w ]+ ⋙ )?Error while connecting to Buster!$", Pattern.CASE_INSENSITIVE);
    public static final Pattern FUY_GG_BUSTER_SUCCESSFULLY_LOGGED_IN =
            Pattern.compile("^([\\w ]+ ⋙ )?Successfully logged into buster!$", Pattern.CASE_INSENSITIVE);

    private ModMessageFilterPatterns() {}
}
