package dev.lotnest.sequoia.feature.features.guildmessagefilter;

import static dev.lotnest.sequoia.utils.Asserter.assertMatches;

public final class GuildMessageFilterPatternsAsserter {
    private GuildMessageFilterPatternsAsserter() {
        assertMatches(
                GuildMessageFilterPatterns.TERRITORY_DEFENSE, "§3§onice pew pew§r§3:§b Panda Path defense is Very Low");
        assertMatches(GuildMessageFilterPatterns.TERRITORY_DEFENSE, "§3Lotnest§r§3:§b Panda Path defense is Very Low");
    }
}
