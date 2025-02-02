/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import com.wynntils.models.territories.type.GuildResource;
import com.wynntils.models.territories.type.GuildResourceValues;
import java.util.Collections;
import java.util.Map;
import net.neoforged.bus.api.Event;

public class TerritoryCapturedEvent extends Event {
    private final String capturingGuild;
    private final String capturedTerritory;
    private final Map<GuildResource, Integer> capturedGenerators;
    private final GuildResourceValues treasury;

    public TerritoryCapturedEvent(
            String capturingGuild,
            String capturedTerritory,
            Map<GuildResource, Integer> capturedGenerators,
            GuildResourceValues treasury) {
        this.capturingGuild = capturingGuild;
        this.capturedTerritory = capturedTerritory;
        this.capturedGenerators = capturedGenerators;
        this.treasury = treasury;
    }

    public String getCapturingGuild() {
        return capturingGuild;
    }

    public String getCapturedTerritory() {
        return capturedTerritory;
    }

    public Map<GuildResource, Integer> getCapturedGenerators() {
        return Collections.unmodifiableMap(capturedGenerators);
    }

    public GuildResourceValues getTreasury() {
        return treasury;
    }
}
