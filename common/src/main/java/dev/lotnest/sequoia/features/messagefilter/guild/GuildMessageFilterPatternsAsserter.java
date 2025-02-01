/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.messagefilter.guild;

import static dev.lotnest.sequoia.utils.Asserter.assertMatches;

public final class GuildMessageFilterPatternsAsserter {
    private GuildMessageFilterPatternsAsserter() {
        // Raid
        assertMatches(
                GuildMessageFilterPatterns.RAID[0],
                "Lhz271, Shisouhan, M1T730, and Kimera00 finished The Nameless Anomaly and claimed 2x Aspects, 2048x Emeralds, and +963m Guild Experience");
        assertMatches(
                GuildMessageFilterPatterns.RAID[0],
                "Lhz271, Shisouhan, M1T730, and Kimera00 finished The Nameless Anomaly and claimed 2x Aspects, 2048x Emeralds, +963m Guild Experience and +410 Seasonal Rating");

        // War
        assertMatches(GuildMessageFilterPatterns.WAR[0], "nice pew pew: Panda Path defense is Very Low");
        assertMatches(GuildMessageFilterPatterns.WAR[0], "Lotnest: Panda Path defense is Very Low");

        // Reward
        assertMatches(GuildMessageFilterPatterns.REWARD[0], "nice pew pew rewarded 1024 Emeralds to llamadile");
        assertMatches(GuildMessageFilterPatterns.REWARD[0], "Lotnest rewarded 1024 Emeralds to llamadile");
        assertMatches(GuildMessageFilterPatterns.REWARD[0], "lindafelix rewarded an Aspect to ScuttleGod");
    }
}
