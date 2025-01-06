package dev.lotnest.sequoia.feature.features.raids;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.wynn.WynnUtils;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

public class RaidsFeature extends Feature {
    private static final Pattern PLAYER_BUFF_CHOSEN =
            Pattern.compile("^(?<player>.+) chosen the (?<buff>.+) (?<buffTier>I|II|III) buff!$");

    private final Map<String, List<Pair<String, String>>> raidBuffs = Maps.newHashMap();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        String unfomattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        if (SequoiaMod.CONFIG.raidsFeature.trackPartyBuffsChosen()) {
            Matcher playerBuffChosenMatcher = PLAYER_BUFF_CHOSEN.matcher(unfomattedMessage);
            if (playerBuffChosenMatcher.matches()) {
                String player = playerBuffChosenMatcher.group("player");
                String buff = playerBuffChosenMatcher.group("buff");
                String buffTier = playerBuffChosenMatcher.group("buffTier");

                raidBuffs.computeIfAbsent(player, k -> Lists.newArrayList()).add(Pair.of(buff, buffTier));
                SequoiaMod.debug("raidsBuffs: " + raidBuffs);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }
}
