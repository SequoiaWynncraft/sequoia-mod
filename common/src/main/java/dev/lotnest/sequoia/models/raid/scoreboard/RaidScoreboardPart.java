/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models.raid.scoreboard;

import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.scoreboard.ScoreboardPart;
import com.wynntils.handlers.scoreboard.ScoreboardSegment;
import com.wynntils.handlers.scoreboard.type.SegmentMatcher;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Models;
import java.util.List;
import java.util.regex.Pattern;

public class RaidScoreboardPart extends ScoreboardPart {
    private static final SegmentMatcher RAID_MATCHER = SegmentMatcher.fromPattern("Raid:");
    private static final Pattern BUFF_PATTERN = Pattern.compile("^Choose a buff or go$");

    @Override
    public SegmentMatcher getSegmentMatcher() {
        return RAID_MATCHER;
    }

    @Override
    public void onSegmentChange(ScoreboardSegment newValue) {
        List<StyledText> content = newValue.getContent();

        if (content.isEmpty()) {
            return;
        }

        SequoiaMod.debug("RaidScoreboardPart: " + content);

        StyledText currentStateLine = content.getFirst();
        if (currentStateLine.matches(BUFF_PATTERN, PartStyle.StyleType.NONE)) {
            Models.Raid.setBuffRoom(Models.Raid.getBuffRoom() + 1);
        }
    }

    @Override
    public void onSegmentRemove(ScoreboardSegment scoreboardSegment) {}

    @Override
    public void reset() {}

    @Override
    public String toString() {
        return null;
    }
}
