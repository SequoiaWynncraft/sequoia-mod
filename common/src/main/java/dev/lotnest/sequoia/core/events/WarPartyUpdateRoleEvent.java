/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import dev.lotnest.sequoia.models.war.WarPartyModel;
import net.neoforged.bus.api.Event;

public class WarPartyUpdateRoleEvent extends Event {
    private final int hash;
    private final String userName;
    private final WarPartyModel.Role role;

    public WarPartyUpdateRoleEvent(int hash, String userName, WarPartyModel.Role role) {
        this.hash = hash;
        this.userName = userName;
        this.role = role;
    }

    public WarPartyModel.Role getRole() {
        return role;
    }

    public String getUserName() {
        return userName;
    }

    public int getHash() {
        return hash;
    }
}
