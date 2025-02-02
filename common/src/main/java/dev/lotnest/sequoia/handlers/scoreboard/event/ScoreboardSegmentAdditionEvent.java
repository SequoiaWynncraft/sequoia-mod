/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.handlers.scoreboard.event;

import dev.lotnest.sequoia.handlers.scoreboard.ScoreboardSegment;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ScoreboardSegmentAdditionEvent extends Event implements ICancellableEvent {
    private final ScoreboardSegment segment;

    public ScoreboardSegmentAdditionEvent(ScoreboardSegment segment) {
        this.segment = segment;
    }

    public ScoreboardSegment getSegment() {
        return segment;
    }
}
