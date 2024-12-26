package dev.lotnest.sequoia.feature.features.discordchatbridge;

import static dev.lotnest.sequoia.feature.features.discordchatbridge.DiscordChatBridgeFeature.GUILD_CHAT_PATTERN;
import static dev.lotnest.sequoia.utils.Asserter.assertMatches;

public final class DiscordChatBridgeFeaturePatternAsserter {
    private DiscordChatBridgeFeaturePatternAsserter() {
        assertMatches(
                GUILD_CHAT_PATTERN,
                "§b\uDAFF\uDFFC\uE001\uDB00\uDC06 \uE060\uDAFF\uDFFF\uE032\uDAFF\uDFFF\uE030\uDAFF\uDFFF\uE03F\uDAFF\uDFFF\uE043\uDAFF\uDFFF\uE030\uDAFF\uDFFF\uE038\uDAFF\uDFFF\uE03D\uDAFF\uDFFF\uE062\uDAFF\uDFD6§0\uE002\uE000\uE00F\uE013\uE000\uE008\uE00D\uDB00\uDC02§b §3Kimera00:§b yurr");
        assertMatches(
                GUILD_CHAT_PATTERN,
                "§b\uDAFF\uDFFC\uE006\uDAFF\uDFFF\uE002\uDAFF\uDFFE \uE060\uDAFF\uDFFF\uE032\uDAFF\uDFFF\uE030\uDAFF\uDFFF\uE03F\uDAFF\uDFFF\uE043\uDAFF\uDFFF\uE030\uDAFF\uDFFF\uE038\uDAFF\uDFFF\uE03D\uDAFF\uDFFF\uE062\uDAFF\uDFD6§0\uE002\uE000\uE00F\uE013\uE000\uE008\uE00D\uDB00\uDC02§b §3§oDemonic Rage§r§3:§b Loot bomb on WC22 with 19m 56s \uDAFF\uDFFC\uE001\uDB00\uDC06 remaining");
        assertMatches(
                GUILD_CHAT_PATTERN,
                "§b\uDAFF\uDFFC\uE001\uDB00\uDC06 \uE060\uDAFF\uDFFF\uE032\uDAFF\uDFFF\uE030\uDAFF\uDFFF\uE03F\uDAFF\uDFFF\uE043\uDAFF\uDFFF\uE030\uDAFF\uDFFF\uE038\uDAFF\uDFFF\uE03D\uDAFF\uDFFF\uE062\uDAFF\uDFD6§0\uE002\uE000\uE00F\uE013\uE000\uE008\uE00D\uDB00\uDC02§b §3§oWHAKAPAKOKO§r§3:§b æøåøæøåøåæøå");
        assertMatches(
                GUILD_CHAT_PATTERN,
                "§b\uDAFF\uDFFC\uE001\uDB00\uDC06 \uE060\uDAFF\uDFFF\uE032\uDAFF\uDFFF\uE030\uDAFF\uDFFF\uE03F\uDAFF\uDFFF\uE043\uDAFF\uDFFF\uE030\uDAFF\uDFFF\uE038\uDAFF\uDFFF\uE03D\uDAFF\uDFFF\uE062\uDAFF\uDFD6§0\uE002\uE000\uE00F\uE013\uE000\uE008\uE00D\uDB00\uDC02§b §3§oGuard§r§3:§b k if we dont get a full party within \uDAFF\uDFFC\uE001\uDB00\uDC06 10 minutes im logging off and gonna \uDAFF\uDFFC\uE001\uDB00\uDC06 take a nap");
    }
}
