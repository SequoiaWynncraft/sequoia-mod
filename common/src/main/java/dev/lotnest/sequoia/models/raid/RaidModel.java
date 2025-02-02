/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.models.raid.event.RaidEndedEvent;
import com.wynntils.utils.mc.StyledTextUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Handlers;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.models.raid.scoreboard.RaidScoreboardPart;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class RaidModel extends Model {
    private static final Pattern RAID_BUFF_CHOSEN_PATTERN = Pattern.compile(
            "§#d6401eff(\\uE009\\uE002|\\uE001) §#fa7f63ff((§o)?(\\w+))§#d6401eff has chosen the §#fa7f63ff(\\w+) (\\w+)§#d6401eff buff!");

    private static final RaidScoreboardPart RAID_SCOREBOARD_PART = new RaidScoreboardPart();

    private final Map<String, Set<Pair<String, String>>> raidBuffs = Maps.newHashMap();
    private int buffRoom = 0;

    public RaidModel() {
        super(List.of());

        Handlers.Scoreboard.addPart(RAID_SCOREBOARD_PART);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidBuffChosen(ChatMessageReceivedEvent event) {
        if (SequoiaMod.CONFIG.raidsFeature.trackChosenPartyBuffs()) {
            Matcher raidBuffChosenMatcher =
                    event.getOriginalStyledText().stripAlignment().getMatcher(RAID_BUFF_CHOSEN_PATTERN);
            if (raidBuffChosenMatcher.matches()) {
                String playerName = raidBuffChosenMatcher.group(4);
                if (raidBuffChosenMatcher.group(3) != null) {
                    playerName = StyledTextUtils.extractNameAndNick(event.getOriginalStyledText())
                            .key();
                    if (StringUtils.isBlank(playerName)) {
                        return;
                    }
                }

                String buff = raidBuffChosenMatcher.group(5);
                String buffTier = raidBuffChosenMatcher.group(6);

                raidBuffs.computeIfAbsent(playerName, k -> Sets.newHashSet()).add(Pair.of(buff, buffTier));
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
