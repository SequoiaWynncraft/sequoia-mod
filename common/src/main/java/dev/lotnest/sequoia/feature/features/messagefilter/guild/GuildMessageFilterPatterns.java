package dev.lotnest.sequoia.feature.features.messagefilter.guild;

import java.util.regex.Pattern;

public final class GuildMessageFilterPatterns {
    // Raid
    public static final Pattern RAID_COMPLETION = Pattern.compile(
            "^(?<player1>[A-Za-z0-9_ ]+?), (?<player2>[A-Za-z0-9_ ]+?), (?<player3>[A-Za-z0-9_ ]+?), and "
                    + "(?<player4>[A-Za-z0-9_ ]+?) finished (?<raid>.+?) and claimed (?<aspects>\\d+)x Aspects, "
                    + "(?<emeralds>\\d+)x Emeralds, (?<xp>.+?m) Guild Experience(?:, and \\+(?<sr>\\d+) Seasonal Rating)?$",
            Pattern.MULTILINE);

    // War
    public static final Pattern TERRITORY_DEFENSE =
            Pattern.compile("^(?<player>.+?): (?<territory>.+?) defense is (?<defense>.+?)$");
    public static final Pattern WAR_STARTING =
            Pattern.compile("^The war for (?<territory>.+?) will start in (?<time>.+?)\\.$");
    public static final Pattern BATTLE_BEGUN = Pattern.compile("^The battle has begun!$");
    public static final Pattern TERRITORY_CAPTURED_BY_OUR_GUILD = Pattern.compile(
            "^You have taken control of (?<territory>.+?) from (?<guild>.+?)! Use /guild territory to defend this territory\\.$");
    public static final Pattern TERRITORY_CAPTURED_BY_OTHER_GUILD =
            Pattern.compile("^(?<guild>.+?) has taken control of (?<territory>.+?)!$");
    public static final Pattern OTHER_GUILD_LOST_WAR = Pattern.compile("^(?<guild>.+?) has lost the war!$");
    public static final Pattern OTHER_GUILD_WAS_VICTORIOUS = Pattern.compile("^(?<guild>.+?) was victorious!$");
    public static final Pattern TERRITORY_DEFENDED =
            Pattern.compile("^Your guild has successfully defended (?<territory>.+?)$");
    public static final Pattern ACTIVE_ATTACK_CANCELED =
            Pattern.compile("^Your active attack was canceled and refunded to your headquarter.$");

    // Economy
    public static final Pattern BONUS_SET =
            Pattern.compile("^(?<player>.+?) set (?<bonus>.+?) bonus to level (?<level>\\d+) on (?<territory>.+?)$");
    public static final Pattern BONUS_REMOVED =
            Pattern.compile("^(?<player>.+?) removed (?<bonus>.+?) bonus from (?<territory>.+?)$");
    public static final Pattern BONUSES_CHANGED =
            Pattern.compile("^(?<player>.+?) changed (?<amount>\\d+) bonuses on (?<territory>.+?)$");
    public static final Pattern UPGRADE_SET = Pattern.compile(
            "^(?<player>.+?) set (?<upgrade>.+?) upgrade to level (?<level>\\d+) on (?<territory>.+?)$");
    public static final Pattern UPGRADE_REMOVED =
            Pattern.compile("^(?<player>.+?) removed (?<upgrade>.+?) upgrade from (?<territory>.+?)$");
    public static final Pattern UPGRADES_CHANGED =
            Pattern.compile("^(?<player>.+?) changed (?<amount>\\d+) upgrades on (?<territory>.+?)$");
    public static final Pattern HEADQUARTERS_SET =
            Pattern.compile("^(?<player>.+?) set the guild headquarters to (?<territory>.+?)$");
    public static final Pattern GLOBAL_STYLE_CHANGED =
            Pattern.compile("^(?<player>.+?) changed the global style to (?<style>.+?)$");
    public static final Pattern TERRITORY_STYLE_CHANGED =
            Pattern.compile("^(?<player>.+?) changed the style of (?<territory>.+?) to (?<style>.+?)$");
    public static final Pattern GLOBAL_TAX_CHANGED =
            Pattern.compile("^(?<player>.+?) changed the global tax to (?<tax>.+?)%$");
    public static final Pattern TERRITORY_TAX_CHANGED =
            Pattern.compile("^(?<player>.+?) changed the tax of (?<territory>.+?) to (?<tax>.+?)%$");
    public static final Pattern PRODUCTION_STABILIZED =
            Pattern.compile("^(?<territory>.+?) production has stabilised$");
    public static final Pattern USING_MORE_RESOURCES_THAN_CAN_STORE =
            Pattern.compile("^(?<territory>.+?) is using more resources than it can store!$");
    public static final Pattern PRODUCING_MORE_RESOURCES_THAN_CAN_STORE =
            Pattern.compile("^(?<territory>.+?) is producing more resources than it can store!$");
    public static final Pattern LOADOUT_APPLIED =
            Pattern.compile("^(?<player>.+?) applied the loadout (?<loadout>.+?) on (?<territory>.+?)$");
    public static final Pattern LOADOUT_UPDATED = Pattern.compile("^(?<player>.+?) updated Loadout (?<loadout>.+?)$");
    public static final Pattern LOADOUT_DELETED = Pattern.compile("^(?<player>.+?) deleted Loadout (?<loadout>.+?)$");
    public static final Pattern TRIBES_SCHEDULE_STOPPED =
            Pattern.compile("^(?<guild1>.+?) stopped scheduling (?<resource>.+?) to (?<guild2>.+?)$");
    public static final Pattern TRIBES_SCHEDULED = Pattern.compile(
            "^(?<guild1>.+?) scheduled (?<resourceIcon>.+?) (?<amount>.+?) (?<resource>.+?) per hour to (?<guild2>.+?)$");
    public static final Pattern GLOBAL_BORDER_CHANGED =
            Pattern.compile("^(?<player>.+?) changed the global borders to (?<style>.+?)$");
    public static final Pattern BORDERS_CHANGED =
            Pattern.compile("^(?<player>.+?) changed the borders of (?<territory>.+?) to (?<style>.+?)$");

    // Reward
    public static final Pattern REWARD =
            Pattern.compile("^(?<player>.+?) rewarded (?<reward>.+?) to (?<recipient>.+?)$");

    // Bank
    public static final Pattern EVERYONE_BANK_DEPOSIT =
            Pattern.compile("^(?<player>.+?) deposited (?<item>.+?) to the Guild Bank \\(Everyone\\)$");
    public static final Pattern EVERYONE_BANK_WITHDRAWAL =
            Pattern.compile("^(?<player>.+?) withdrew (?<item>.+?) from the Guild Bank \\(Everyone\\)$");
    public static final Pattern HIGH_RANKED_BANK_DEPOSIT =
            Pattern.compile("^(?<player>.+?) deposited (?<item>.+?) to the Guild Bank \\(High Ranked\\)$");
    public static final Pattern HIGH_RANKED_BANK_WITHDRAWAL =
            Pattern.compile("^(?<player>.+?) withdrew (?<item>.+?) from the Guild Bank \\(High Ranked\\)$");

    // Rank
    public static final Pattern RANK_SET =
            Pattern.compile("^(?<player>.+?) has set (?<player2>.+?) guild rank from (?<rank>.+?) to (?<rank2>.+?)$");

    private GuildMessageFilterPatterns() {}
}
