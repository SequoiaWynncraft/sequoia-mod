/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import dev.lotnest.sequoia.features.guildraidtracker.GuildRaid;
import net.neoforged.bus.api.Event;

public class GuildRaidCompletedEvent extends Event {
    private final GuildRaid guildRaid;

    public GuildRaidCompletedEvent(GuildRaid guildRaid) {
        this.guildRaid = guildRaid;
    }

    public GuildRaid getGuildRaid() {
        return guildRaid;
    }
}
