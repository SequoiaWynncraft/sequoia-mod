/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models.war;

import com.google.common.collect.Maps;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.core.events.WarPartyCreatedEvent;
import dev.lotnest.sequoia.core.events.WarPartyDisbandEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateRequestEvent;
import dev.lotnest.sequoia.features.war.GuildWarParty;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class WarPartyModel extends Model {
    private final Map<Integer, GuildWarParty> activeWarParties = Maps.newHashMap();

    public WarPartyModel() {
        super(List.of());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void handlePartyCreation(WarPartyCreatedEvent event) {
        int partyId = event.getHash();
        String leader = event.getPartyLeader();
        Map<String, Role> members = Maps.newHashMap();
        String territory = event.getTerritory();
        Role leaderRole = event.getRole();

        members.put(leader, leaderRole);

        GuildWarParty guildWarParty = new GuildWarParty(partyId, leader, territory, members);
        activeWarParties.putIfAbsent(partyId, guildWarParty);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void handlePartyDisband(WarPartyDisbandEvent event) {
        activeWarParties.remove(event.getHash());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void handlePartyUpdate(WarPartyUpdateEvent event) {
        String userName = event.getUserName();
        Role role = event.getRole();
        int partyId = event.getHash();

        activeWarParties.computeIfPresent(partyId, (id, party) -> {
            if (event.getOperation() > 1) {
                party.members().put(userName, role);
            } else {
                party.members().remove(userName);
            }
            return party;
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void handlePartyUpdateRequest(WarPartyUpdateRequestEvent event) {
        // TODO: Implement
    }

    public Map<Integer, GuildWarParty> getActiveWarParties() {
        return Collections.unmodifiableMap(activeWarParties);
    }

    public enum Role {
        SOLO("Solo"),
        DPS("DPS"),
        TANK("Tank"),
        HEALER("Healer");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Role fromString(String roleName) {
            for (Role role : values()) {
                if (role.displayName.equalsIgnoreCase(roleName)) {
                    return role;
                }
            }
            return null;
        }
    }
}
