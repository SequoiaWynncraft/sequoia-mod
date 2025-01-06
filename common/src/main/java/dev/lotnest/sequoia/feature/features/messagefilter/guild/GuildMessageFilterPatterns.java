package dev.lotnest.sequoia.feature.features.messagefilter.guild;

import java.util.regex.Pattern;

public final class GuildMessageFilterPatterns {
    public static final Pattern[] RAID = {
        Pattern.compile(
                "^(?<player1>[A-Za-z0-9_ ]+?), (?<player2>[A-Za-z0-9_ ]+?), (?<player3>[A-Za-z0-9_ ]+?), and "
                        + "(?<player4>[A-Za-z0-9_ ]+?) finished (?<raid>.+?) and claimed (?<aspects>\\d+)x Aspects, "
                        + "(?<emeralds>\\d+)x Emeralds, (?<xp>.+?m) Guild Experience(?:, and \\+(?<sr>\\d+) Seasonal Rating)?$",
                Pattern.MULTILINE)
    };

    public static final Pattern[] WAR = {
        Pattern.compile("^(?<player>.+?): (?<territory>.+?) defense is (?<defense>.+?)$"),
        Pattern.compile("^The war for (?<territory>.+?) will start in (?<time>.+?)\\.$"),
        Pattern.compile("^The battle has begun!$"),
        Pattern.compile(
                "^You have taken control of (?<territory>.+?) from (?<guild>.+?)! Use /guild territory to defend this territory\\.$"),
        Pattern.compile("^(?<guild>.+?) has taken control of (?<territory>.+?)!$"),
        Pattern.compile("^(?<guild>.+?) has lost the war!$"),
        Pattern.compile("^(?<guild>.+?) was victorious!$"),
        Pattern.compile("^Your guild has successfully defended (?<territory>.+?)$"),
        Pattern.compile("^Your active attack was canceled and refunded to your headquarter.$")
    };

    public static final Pattern[] ECONOMY = {
        Pattern.compile("^(?<player>.+?) set (?<bonus>.+?) bonus to level (?<level>\\d+) on (?<territory>.+?)$"),
        Pattern.compile("^(?<player>.+?) removed (?<bonus>.+?) bonus from (?<territory>.+?)$"),
        Pattern.compile("^(?<player>.+?) changed (?<amount>\\d+) bonuses on (?<territory>.+?)$"),
        Pattern.compile("^(?<player>.+?) set (?<upgrade>.+?) upgrade to level (?<level>\\d+) on (?<territory>.+?)$"),
        Pattern.compile("^(?<player>.+?) removed (?<upgrade>.+?) upgrade from (?<territory>.+?)$"),
        Pattern.compile("^(?<player>.+?) changed (?<amount>\\d+) upgrades on (?<territory>.+?)$"),
        Pattern.compile("^(?<player>.+?) set the guild headquarters to (?<territory>.+?)$"),
        Pattern.compile("^(?<player>.+?) changed the global style to (?<style>.+?)$"),
        Pattern.compile("^(?<player>.+?) changed the style of (?<territory>.+?) to (?<style>.+?)$"),
        Pattern.compile("^(?<player>.+?) changed the global tax to (?<tax>.+?)%$"),
        Pattern.compile("^(?<player>.+?) changed the tax of (?<territory>.+?) to (?<tax>.+?)%$"),
        Pattern.compile("^(?<territory>.+?) production has stabilised$"),
        Pattern.compile("^(?<territory>.+?) is using more resources than it can store!$"),
        Pattern.compile("^(?<territory>.+?) is producing more resources than it can store!$"),
        Pattern.compile("^(?<player>.+?) applied the loadout (?<loadout>.+?) on (?<territory>.+?)$"),
        Pattern.compile("^(?<player>.+?) updated Loadout (?<loadout>.+?)$"),
        Pattern.compile("^(?<player>.+?) deleted Loadout (?<loadout>.+?)$"),
        Pattern.compile("^(?<guild1>.+?) stopped scheduling (?<resource>.+?) to (?<guild2>.+?)$"),
        Pattern.compile(
                "^(?<guild1>.+?) scheduled (?<resourceIcon>.+?) (?<amount>.+?) (?<resource>.+?) per hour to (?<guild2>.+?)$"),
        Pattern.compile("^(?<player>.+?) changed the global borders to (?<style>.+?)$"),
        Pattern.compile("^(?<player>.+?) changed the borders of (?<territory>.+?) to (?<style>.+?)$"),
        Pattern.compile("^(?<player>.+?) sent (?<guild>.+?) a request to be allied$")
    };

    public static final Pattern[] REWARD = {
        Pattern.compile("^(?<player>.+?) rewarded (?<reward>.+?) to (?<recipient>.+?)$")
    };

    public static final Pattern[] BANK = {
        Pattern.compile("^(?<player>.+?) deposited (?<item>.+?) to the Guild Bank \\(Everyone\\)$"),
        Pattern.compile("^(?<player>.+?) withdrew (?<item>.+?) from the Guild Bank \\(Everyone\\)$"),
        Pattern.compile("^(?<player>.+?) deposited (?<item>.+?) to the Guild Bank \\(High Ranked\\)$"),
        Pattern.compile("^(?<player>.+?) withdrew (?<item>.+?) from the Guild Bank \\(High Ranked\\)$")
    };

    public static final Pattern[] RANK = {
        Pattern.compile("^(?<player>.+?) has set (?<player2>.+?) guild rank from (?<rank>.+?) to (?<rank2>.+?)$")
    };

    private GuildMessageFilterPatterns() {}
}
