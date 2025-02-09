/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import dev.lotnest.sequoia.models.War.WarPartyModel;
import net.neoforged.bus.api.Event;

public class WarPartyUpdateEvent extends Event {
    private final int hash;
    private final String userName;
    private final int operation;
    private final WarPartyModel.Role role;

    public WarPartyUpdateEvent(int hash, String userName, int operation, WarPartyModel.Role role) {
        this.hash = hash;
        this.userName = userName;
        this.operation = operation;
        this.role = role;
    }

    public int getHash() {
        return this.hash;
    }

    public String getUserName() {
        return this.userName;
    }

    public int getOperation() {
        return this.operation;
    }

    public WarPartyModel.Role getRole() {
        return this.role;
    }
}
