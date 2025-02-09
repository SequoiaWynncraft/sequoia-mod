/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import dev.lotnest.sequoia.models.war.WarPartyModel;
import net.neoforged.bus.api.Event;

public class WarPartyCreatedEvent extends Event {
    private final int hash;
    private final String partyLeader;
    private final String territory;
    private final WarPartyModel.Role role;

    public WarPartyCreatedEvent(int hash, String partyLeader, String territory, WarPartyModel.Role role) {
        this.hash = hash;
        this.partyLeader = partyLeader;
        this.territory = territory;
        this.role = role;
    }

    public int getHash() {
        return this.hash;
    }

    public String getPartyLeader() {
        return this.partyLeader;
    }

    public String getTerritory() {
        return this.territory;
    }

    public WarPartyModel.Role getRole() {
        return this.role;
    }
}
