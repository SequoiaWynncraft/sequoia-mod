package dev.lotnest.sequoia.feature.features;

import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class PlayerIgnoreFeature extends Feature {
    private static final Pattern GUILD_CHAT_PATTERN = Pattern.compile(
            "^(?:(?:§b)?(?:\uDAFF\uDFFC\uE006\uDAFF\uDFFF\uE002\uDAFF\uDFFE|\uDAFF\uDFFC\uE001\uDB00\uDC07)\\s)?(?:§o[^§]+§c\\s*\\((?<nicknameOrUsername>[^)]+)\\)§3|(?<username>[^:]+)): §b(?<message>.+)$");

    private static final Pattern SHOUT_PATTERN =
            Pattern.compile("^§5(?<player>.+?) \\[(?<server>[A-Z0-9]+)] shouts: §d(?<message>.+)$");

    private static final Pattern PARTY_PATTERN = Pattern.compile(
            "^(?:(?:§e)?(?:\uDAFF\uDFFC\uE005\uDAFF\uDFFF\uE002\uDAFF\uDFFE|\uDAFF\uDFFC\uE001\uDB00\uDC06)\\s)?(?:§o[^§]+§c\\s*\\((?<nicknameOrUsername>[^)]+)\\)§e|(?<username>[^:]+)): §f(?<message>.+)$");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMessage(ChatMessageReceivedEvent event) {
        Matcher guildChatMatcher = event.getStyledText().getMatcher(GUILD_CHAT_PATTERN);
        if (guildChatMatcher.matches()) {
            String nicknameOrUsername = guildChatMatcher.group("nicknameOrUsername");
            String username = guildChatMatcher.group("username");
            nicknameOrUsername = StringUtils.isBlank(username) ? nicknameOrUsername : username;

            if (SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers().stream()
                    .anyMatch(nicknameOrUsername::equalsIgnoreCase)) {
                event.setCanceled(true);
            }
        }

        Matcher shoutMatcher = event.getStyledText().getMatcher(SHOUT_PATTERN);
        if (shoutMatcher.matches()) {
            String player = shoutMatcher.group("player").trim();

            if (SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers().stream().anyMatch(player::equalsIgnoreCase)) {
                event.setCanceled(true);
            }
        }

        Matcher partyMatcher = event.getStyledText().getMatcher(PARTY_PATTERN);
        if (partyMatcher.matches()) {
            String nicknameOrUsername = partyMatcher.group("nicknameOrUsername");
            String username = partyMatcher.group("username");
            nicknameOrUsername = StringUtils.isBlank(username) ? nicknameOrUsername : username;

            if (SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers().stream()
                    .anyMatch(nicknameOrUsername::equalsIgnoreCase)) {
                event.setCanceled(true);
            }
        }
    }
}
