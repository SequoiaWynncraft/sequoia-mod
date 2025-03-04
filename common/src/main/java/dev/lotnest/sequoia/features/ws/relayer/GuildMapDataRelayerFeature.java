/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.ws.relayer;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.wynntils.core.components.Models;
import com.wynntils.models.territories.type.GuildResource;
import com.wynntils.services.map.pois.TerritoryPoi;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.core.ws.message.istateopcodes.GuildMapDataIStateOpCode;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GuildMapDataRelayerFeature extends Feature {
    public GuildMapDataRelayerFeature() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::relayGuildMapData, 0, 10, TimeUnit.MINUTES);
    }

    private void relayGuildMapData() {
        if (!isEnabled()) {
            return;
        }

        if (!Models.Character.hasCharacter()) {
            return;
        }

        doRelayGuildMapData();
    }

    private void doRelayGuildMapData() {
        Map<String, GuildMapDataIStateOpCode.Data.TerritoryData> territoryDataMap =
                Models.Territory.getTerritoryPoisFromAdvancement().stream()
                        .map(this::mapToFriendlyTerritoryNameTerritoryDataPair)
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        GuildMapDataIStateOpCode.Data guildMapDataIStateOpCodeData =
                new GuildMapDataIStateOpCode.Data(territoryDataMap, OffsetDateTime.now());
        GuildMapDataIStateOpCode guildMapDataIStateOpCode = new GuildMapDataIStateOpCode(guildMapDataIStateOpCodeData);

        SequoiaMod.getWebSocketFeature().sendMessage(guildMapDataIStateOpCode);
    }

    private Pair<String, GuildMapDataIStateOpCode.Data.TerritoryData> mapToFriendlyTerritoryNameTerritoryDataPair(
            TerritoryPoi territoryPoi) {
        return Pair.of(
                territoryPoi.getTerritoryProfile().getFriendlyName(),
                new GuildMapDataIStateOpCode.Data.TerritoryData(
                        territoryPoi.getTerritoryInfo().getGuildName(),
                        territoryPoi.getTerritoryInfo().getGuildPrefix(),
                        (byte) territoryPoi.getTerritoryInfo().getTreasury().getLevel(),
                        new GuildMapDataIStateOpCode.Data.TerritoryData.Stored(
                                territoryPoi.getTerritoryInfo().getGuildName(),
                                territoryPoi
                                        .getTerritoryInfo()
                                        .getStorage(GuildResource.EMERALDS)
                                        .current(),
                                territoryPoi
                                        .getTerritoryInfo()
                                        .getStorage(GuildResource.ORE)
                                        .current(),
                                territoryPoi
                                        .getTerritoryInfo()
                                        .getStorage(GuildResource.WOOD)
                                        .current(),
                                territoryPoi
                                        .getTerritoryInfo()
                                        .getStorage(GuildResource.CROPS)
                                        .current(),
                                territoryPoi
                                        .getTerritoryInfo()
                                        .getStorage(GuildResource.FISH)
                                        .current()),
                        new GuildMapDataIStateOpCode.Data.TerritoryData.Generation(
                                territoryPoi.getTerritoryInfo().getGuildName(),
                                territoryPoi.getTerritoryInfo().getGeneration(GuildResource.EMERALDS),
                                territoryPoi.getTerritoryInfo().getGeneration(GuildResource.ORE),
                                territoryPoi.getTerritoryInfo().getGeneration(GuildResource.WOOD),
                                territoryPoi.getTerritoryInfo().getGeneration(GuildResource.CROPS),
                                territoryPoi.getTerritoryInfo().getGeneration(GuildResource.FISH))));
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.getWebSocketFeature().isEnabled() && SequoiaMod.CONFIG.webSocketFeature.relayGuildMapData();
    }
}
