/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import net.neoforged.bus.api.Event;

public class WarPartyDisbandEvent extends Event {
    private final int hash;

    public WarPartyDisbandEvent(int hash) {
        this.hash = hash;
    }

    public int getHash() {
        return this.hash;
    }
}
