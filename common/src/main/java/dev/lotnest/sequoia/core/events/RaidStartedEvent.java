/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import net.neoforged.bus.api.Event;

public class RaidStartedEvent extends Event {
    private final long timestamp;

    public RaidStartedEvent() {
        this(System.currentTimeMillis());
    }

    public RaidStartedEvent(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
