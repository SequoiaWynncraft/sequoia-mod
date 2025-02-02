/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.bus.api.Event;

public class ScoreboardSetObjectiveEvent extends Event {
    public static final int METHOD_ADD = 0;
    public static final int METHOD_REMOVE = 1;
    public static final int METHOD_CHANGE = 2;
    private final String objectiveName;
    private final Component displayName;
    private final ObjectiveCriteria.RenderType renderType;
    private final int method;

    public ScoreboardSetObjectiveEvent(
            String objectiveName, Component displayName, ObjectiveCriteria.RenderType renderType, int method) {
        this.objectiveName = objectiveName;
        this.displayName = displayName;
        this.renderType = renderType;
        this.method = method;
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public ObjectiveCriteria.RenderType getRenderType() {
        return this.renderType;
    }

    public int getMethod() {
        return this.method;
    }
}
