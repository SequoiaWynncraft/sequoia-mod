/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.event;

import net.minecraft.world.scores.DisplaySlot;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ScoreboardSetDisplayObjectiveEvent extends Event implements ICancellableEvent {
    private final DisplaySlot slot;
    private final String objectiveName;

    public ScoreboardSetDisplayObjectiveEvent(DisplaySlot slot, String objectiveName) {
        this.slot = slot;
        this.objectiveName = objectiveName;
    }

    public DisplaySlot getSlot() {
        return this.slot;
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }
}
