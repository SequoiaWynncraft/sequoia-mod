/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.mc.event.TitleSetTextEvent;
import com.wynntils.models.raid.event.RaidEndedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

public class RaidModel extends Model {
    private static final Pattern PLAYER_BUFF_CHOSEN_PATTERN =
            Pattern.compile("^(?<player>.+) chosen the (?<buff>.+) (?<buffTier>I|II|III) buff!$");

    private static final Pattern RAID_COMPLETED_PATTERN = Pattern.compile("§f§lR§#4d4d4dff§laid Completed!");
    private static final Pattern RAID_FAILED_PATTERN = Pattern.compile("§4§kRa§c§lid Failed!");

    private final Map<String, Set<Pair<String, String>>> raidBuffs = Maps.newHashMap();

    public RaidModel() {
        super(List.of());
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
    public void raidEndedEvent(RaidEndedEvent event) {
        raidBuffs.clear();
    }

    public Map<String, Set<Pair<String, String>>> getRaidBuffs() {
        return Collections.unmodifiableMap(raidBuffs);
    }

    public Set<Pair<String, String>> getRaidBuffs(String player) {
        return raidBuffs.getOrDefault(player, Collections.emptySet());
    }
}
