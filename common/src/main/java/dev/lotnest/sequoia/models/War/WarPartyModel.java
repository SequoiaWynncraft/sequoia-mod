/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models.War;

import com.google.common.collect.Maps;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.core.events.WarPartyCreatedEvent;
import dev.lotnest.sequoia.core.events.WarPartyDisbandEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateRequestEvent;
import dev.lotnest.sequoia.features.war.GuildWarParty;
import java.util.List;
import java.util.Map;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class WarPartyModel extends Model {
    private final Map<Integer, GuildWarParty> warParties = Maps.newHashMap();

    public WarPartyModel() {
        super(List.of());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPartyCreate(WarPartyCreatedEvent event) {
        int hash = event.getHash();
        String leader = event.getPartyLeader();
        Map<String, Role> members = Maps.newHashMap();
        String territory = event.getTerritory();
        Role position = event.getRole();
        members.put(leader, position);
        GuildWarParty party = new GuildWarParty(hash, leader, territory, members);
        if (warParties.containsKey(hash)) {
            return;
        }
        warParties.put(hash, party);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPartyDisband(WarPartyDisbandEvent event) {
        warParties.remove(event.getHash());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPartyUpdate(WarPartyUpdateEvent event) {
        String userName = event.getUserName();
        Role position = event.getRole();
        warParties.compute(event.getHash(), (k, v) -> {
            if (v != null && v.hash() == event.getHash()) {
                if (event.getOperation() > 1) {
                    v.members().put(userName, position);
                } else {
                    v.members().remove(userName);
                }
                return v;
            }
            return v;
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPartyUpdateRequest(WarPartyUpdateRequestEvent event) {}

    public enum Role {
        DPS("Dps"),
        TANK("Tank"),
        HEALER("Healer");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return getDisplayName();
        }

        public String getDisplayName() {
            return displayName;
        }

        public static WarPartyModel.Role fromString(String string) {
            WarPartyModel.Role[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                WarPartyModel.Role value = var1[var3];
                if (value.displayName.equals(string)) {
                    return value;
                }
            }

            return null;
        }
    }
}
