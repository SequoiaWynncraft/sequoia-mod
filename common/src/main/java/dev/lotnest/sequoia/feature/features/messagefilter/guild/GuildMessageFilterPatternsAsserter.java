package dev.lotnest.sequoia.feature.features.messagefilter.guild;

import static dev.lotnest.sequoia.utils.Asserter.assertMatches;

public final class GuildMessageFilterPatternsAsserter {
    private GuildMessageFilterPatternsAsserter() {
        // Raid
        assertMatches(
                GuildMessageFilterPatterns.RAID_COMPLETION,
                "Lhz271, Shisouhan, M1T730, and Kimera00 finished The Nameless Anomaly and claimed 2x Aspects, 2048x Emeralds, and +963m Guild Experience");
        assertMatches(
                GuildMessageFilterPatterns.RAID_COMPLETION,
                "Lhz271, Shisouhan, M1T730, and Kimera00 finished The Nameless Anomaly and claimed 2x Aspects, 2048x Emeralds, +963m Guild Experience and +410 Seasonal Rating");

        // War
        assertMatches(GuildMessageFilterPatterns.TERRITORY_DEFENSE, "nice pew pew: Panda Path defense is Very Low");
        assertMatches(GuildMessageFilterPatterns.TERRITORY_DEFENSE, "Lotnest: Panda Path defense is Very Low");

        // Reward
        assertMatches(GuildMessageFilterPatterns.REWARD, "nice pew pew rewarded 1024 Emeralds to llamadile");
        assertMatches(GuildMessageFilterPatterns.REWARD, "Lotnest rewarded 1024 Emeralds to llamadile");
        assertMatches(GuildMessageFilterPatterns.REWARD, "lindafelix rewarded an Aspect to ScuttleGod");
    }
}
