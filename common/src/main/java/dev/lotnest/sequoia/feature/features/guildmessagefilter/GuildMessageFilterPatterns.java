package dev.lotnest.sequoia.feature.features.guildmessagefilter;

import java.util.regex.Pattern;

public final class GuildMessageFilterPatterns {
    // Raid
    public static final Pattern RAID_COMPLETION = Pattern.compile(
            "(?<player1>[A-Za-z0-9_ ]+?), (?<player2>[A-Za-z0-9_ ]+?), (?<player3>[A-Za-z0-9_ ]+?), and "
                    + "(?<player4>[A-Za-z0-9_ ]+?) finished (?<raid>.+?) and claimed (?<aspects>\\d+)x Aspects, "
                    + "(?<emeralds>\\d+)x Emeralds, (?<xp>.+?m) Guild Experience(?:, and \\+(?<sr>\\d+) Seasonal Rating)?",
            Pattern.MULTILINE);

    // War
    public static final Pattern TERRITORY_DEFENSE =
            Pattern.compile("(?<player>.*?): (?<territory>.*?) defense is (?<defense>.*?)");
    public static final Pattern WAR_STARTING =
            Pattern.compile("The war for (?<territory>.*?) will start in (?<time>.*?)\\.");
    public static final Pattern BATTLE_BEGUN = Pattern.compile("The battle has begun!");
    public static final Pattern TERRITORY_CAPTURED_BY_OUR_GUILD = Pattern.compile(
            "You have taken control of (?<territory>.*?) from (?<guild>.*?)! Use /guild territory to defend this territory\\.");
    public static final Pattern TERRITORY_CAPTURED_BY_OTHER_GUILD =
            Pattern.compile("(?<guild>.*?) has taken control of (?<territory>.*?)!");
    public static final Pattern OTHER_GUILD_LOST_WAR = Pattern.compile("(?<guild>.*?) has lost the war!");
    public static final Pattern TERRITORY_SUCCESSFULLY_DEFENDED =
            Pattern.compile("Your guild has successfully defended (?<territory>.*?)");

    // Economy
    public static final Pattern BONUSES_CHANGED =
            Pattern.compile("(?<player>.*?) changed (?<amount>\\d+) bonuses on (?<territory>.*?)");
    public static final Pattern UPGRADES_CHANGED =
            Pattern.compile("(?<player>.*?) changed (?<amount>\\d+) upgrades on (?<territory>.*?)");
    public static final Pattern HEADQUARTERS_CHANGED =
            Pattern.compile("(?<player>.*?) set the guild headquarters to (?<territory>.*?)");
    public static final Pattern GLOBAL_STYLE_CHANGED =
            Pattern.compile("(?<player>.*?) changed the global style to (?<style>.*?)");
    public static final Pattern GLOBAL_TAX_CHANGED =
            Pattern.compile("(?<player>.*?) changed the global tax to (?<tax>.*?)");
    public static final Pattern PRODUCTION_STABILIZED = Pattern.compile("(?<territory>.*?) production has stabilised");
    public static final Pattern USING_MORE_RESOURCES_THAN_CAN_STORE =
            Pattern.compile("(?<territory>.*?) is using more resources than it can store!");
    public static final Pattern PRODUCING_MORE_RESOURCES_THAN_CAN_STORE =
            Pattern.compile("(?<territory>.*?) is producing more resources than it can store!");

    // Reward
    public static final Pattern REWARD = Pattern.compile("(?<player>.*?) rewarded (?<reward>.*?) to (?<recipient>.*?)");

    private GuildMessageFilterPatterns() {}
}
