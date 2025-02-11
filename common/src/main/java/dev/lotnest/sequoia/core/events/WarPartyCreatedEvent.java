/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import dev.lotnest.sequoia.models.war.WarModel;
import dev.lotnest.sequoia.models.war.WarPartyModel;
import net.neoforged.bus.api.Event;

public class WarPartyCreatedEvent extends Event {
    private final int hash;
    private final String partyLeader;
    private final String territory;
    private final WarPartyModel.Role role;
    private final WarModel.Difficulty difficulty;

    public WarPartyCreatedEvent(
            int hash, String partyLeader, String territory, WarPartyModel.Role role, WarModel.Difficulty difficulty) {
        this.hash = hash;
        this.partyLeader = partyLeader;
        this.territory = territory;
        this.role = role;
        this.difficulty = difficulty;
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

    public WarModel.Difficulty getDifficulty() {
        return difficulty;
    }
}
