/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import net.neoforged.bus.api.Event;

public class PartyPlayerJoinedEvent extends Event {
    private final String playerName;

    public PartyPlayerJoinedEvent(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
