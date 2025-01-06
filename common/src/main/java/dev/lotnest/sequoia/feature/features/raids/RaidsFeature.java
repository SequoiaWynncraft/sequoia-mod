package dev.lotnest.sequoia.feature.features.raids;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.wynn.WynnUtils;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaidsFeature extends Feature {
    private static final Pattern PLAYER_BUFF_CHOSEN =
            Pattern.compile("^(?<player>.+) chosen the (?<buff>.+) (?<buffTier>I|II|III) buff!$");

    private final Map<String, Set<Pair<String, String>>> raidBuffs = Maps.newHashMap();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        String unfomattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        if (SequoiaMod.CONFIG.raidsFeature.trackChosenPartyBuffs()) {
            Matcher playerBuffChosenMatcher = PLAYER_BUFF_CHOSEN.matcher(unfomattedMessage);
            if (playerBuffChosenMatcher.matches()) {
                String player = playerBuffChosenMatcher.group("player");
                String buff = playerBuffChosenMatcher.group("buff");
                String buffTier = playerBuffChosenMatcher.group("buffTier");

                raidBuffs.computeIfAbsent(player, k -> Sets.newHashSet()).add(Pair.of(buff, buffTier));

                SequoiaMod.debug("raidsBuffs: " + raidBuffs);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }
}
