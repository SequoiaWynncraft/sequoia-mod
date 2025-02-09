/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import net.neoforged.bus.api.Event;

public class WarPartyUpdateRequestEvent extends Event {
    private final String userName;

    public WarPartyUpdateRequestEvent(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}
