package dev.lotnest.sequoia.feature.features.guildmessagefilter;

import java.util.regex.Pattern;

public final class GuildMessageFilterPatterns {
    public static final Pattern TERRITORY_DEFENSE = Pattern.compile(
            "§3(?:§o)?(?<player>.*?)§r§3:§b (?<territory>.*?) defense is (?<defense>Very Low|Low|Medium|High|Very High)");

    private GuildMessageFilterPatterns() {}
}
