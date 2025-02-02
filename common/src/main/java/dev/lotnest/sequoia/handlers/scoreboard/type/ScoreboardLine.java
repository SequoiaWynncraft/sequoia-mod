/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.handlers.scoreboard.type;

import com.wynntils.core.text.StyledText;

public record ScoreboardLine(StyledText line, int score) implements Comparable<ScoreboardLine> {
    @Override
    public int compareTo(ScoreboardLine other) {
        // Negate the result because we want the highest score to be first
        return -Integer.compare(score, other.score);
    }
}
