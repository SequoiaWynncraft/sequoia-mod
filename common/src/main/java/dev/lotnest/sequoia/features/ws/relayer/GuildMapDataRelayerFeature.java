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
import dev.lotnest.sequoia.core.ws.message.ws.GIStateUpdateWSMessage;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GuildMapDataRelayerFeature extends Feature {
    public GuildMapDataRelayerFeature() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::relayGuildMapData, 0, 10, TimeUnit.MINUTES);
    }

    public void relayGuildMapData() {
        try {
            SequoiaMod.debug("Checking if guild map data relay is possible");

            if (!isEnabled()) return;
            if (!Models.Character.hasCharacter()) return;

            doRelayGuildMapData();
        } catch (Exception exception) {
            SequoiaMod.error("Failed to relay guild map data", exception);
        }
    }

    private void doRelayGuildMapData() {
        SequoiaMod.debug("Gathering guild map data for relay");

        Map<String, GuildMapDataIStateOpCode.Data.TerritoryData> territoryDataMap =
                Models.Territory.getTerritoryPoisFromAdvancement().stream()
                        .filter(Objects::nonNull)
                        .map(this::mapToFriendlyTerritoryNameTerritoryDataPair)
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        GuildMapDataIStateOpCode.Data guildMapDataIStateOpCodeData =
                new GuildMapDataIStateOpCode.Data(territoryDataMap, OffsetDateTime.now());
        GuildMapDataIStateOpCode guildMapDataIStateOpCode = new GuildMapDataIStateOpCode(guildMapDataIStateOpCodeData);
        GIStateUpdateWSMessage giStateUpdateWSMessage =
                new GIStateUpdateWSMessage(new GIStateUpdateWSMessage.Data(guildMapDataIStateOpCode));

        SequoiaMod.getWebSocketFeature().sendMessage(giStateUpdateWSMessage);
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
                                getStorageSafe(territoryPoi, GuildResource.EMERALDS),
                                getStorageSafe(territoryPoi, GuildResource.ORE),
                                getStorageSafe(territoryPoi, GuildResource.WOOD),
                                getStorageSafe(territoryPoi, GuildResource.CROPS),
                                getStorageSafe(territoryPoi, GuildResource.FISH)),
                        new GuildMapDataIStateOpCode.Data.TerritoryData.Generation(
                                territoryPoi.getTerritoryInfo().getGuildName(),
                                getGenerationSafe(territoryPoi, GuildResource.EMERALDS),
                                getGenerationSafe(territoryPoi, GuildResource.ORE),
                                getGenerationSafe(territoryPoi, GuildResource.WOOD),
                                getGenerationSafe(territoryPoi, GuildResource.CROPS),
                                getGenerationSafe(territoryPoi, GuildResource.FISH))));
    }

    private int getStorageSafe(TerritoryPoi territoryPoi, GuildResource resource) {
        return territoryPoi.getTerritoryInfo().getStorage(resource) != null
                ? territoryPoi.getTerritoryInfo().getStorage(resource).current()
                : 0;
    }

    private int getGenerationSafe(TerritoryPoi territoryPoi, GuildResource resource) {
        Integer generation = territoryPoi.getTerritoryInfo().getGeneration(resource);
        return generation != null ? generation : 0;
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.getWebSocketFeature().isEnabled() && SequoiaMod.CONFIG.webSocketFeature.relayGuildMapData();
    }
}
