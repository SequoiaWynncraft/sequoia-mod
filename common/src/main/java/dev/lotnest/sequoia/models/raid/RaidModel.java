/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.models.raid.event.RaidEndedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Handlers;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.models.raid.scoreboard.RaidScoreboardPart;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

public class RaidModel extends Model {
    private static final Pattern PLAYER_BUFF_CHOSEN_PATTERN =
            Pattern.compile("^(?<player>.+) chosen the (?<buff>.+) (?<buffTier>I|II|III) buff!$");
    private static final RaidScoreboardPart RAID_SCOREBOARD_PART = new RaidScoreboardPart();

    private final Map<String, Set<Pair<String, String>>> raidBuffs = Maps.newHashMap();
    private int buffRoom = 0;

    public RaidModel() {
        super(List.of());

        Handlers.Scoreboard.addPart(RAID_SCOREBOARD_PART);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidBuffChosen(ChatMessageReceivedEvent event) {
        String unfomattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        if (SequoiaMod.CONFIG.raidsFeature.trackChosenPartyBuffs()) {
            Matcher playerBuffChosenMatcher = PLAYER_BUFF_CHOSEN_PATTERN.matcher(unfomattedMessage);
            if (playerBuffChosenMatcher.matches()) {
                String player = playerBuffChosenMatcher.group("player");
                String buff = playerBuffChosenMatcher.group("buff");
                String buffTier = playerBuffChosenMatcher.group("buffTier");

                raidBuffs.computeIfAbsent(player, k -> Sets.newHashSet()).add(Pair.of(buff, buffTier));
                SequoiaMod.debug("raidBuffs: " + raidBuffs);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidCompletedEvent(RaidEndedEvent.Completed event) {
        raidBuffs.clear();
        buffRoom = 0;
        SequoiaMod.debug("Clearing raidBuffs and buffRoom as raid has completed");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidFailedEvent(RaidEndedEvent.Failed event) {
        raidBuffs.clear();
        buffRoom = 0;
        SequoiaMod.debug("Clearing raidBuffs and buffRoom as raid has failed");
    }

    public Map<String, Set<Pair<String, String>>> getRaidBuffs() {
        return Collections.unmodifiableMap(raidBuffs);
    }

    public Set<Pair<String, String>> getRaidBuffs(String player) {
        return raidBuffs.getOrDefault(player, Collections.emptySet());
    }

    public boolean isInBuffRoom() {
        return buffRoom > 0;
    }

    public int getBuffRoom() {
        return buffRoom;
    }

    public void setBuffRoom(int buffRoom) {
        this.buffRoom = buffRoom;
        SequoiaMod.debug("buffRoom: " + buffRoom);
    }
}
