/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.services.wynn.player;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Service;
import dev.lotnest.sequoia.core.components.Services;
import dev.lotnest.sequoia.core.http.HttpClients;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerService extends Service {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/player/%s";
    private static final String FULL_RESULT_URL = BASE_URL + "?fullResult";

    public PlayerService() {
        super(List.of());
    }

    public CompletableFuture<PlayerResponse> getPlayer(String username) {
        String url = String.format(BASE_URL, username);
        return HttpClients.WYNNCRAFT_API.getJsonAsync(url, PlayerResponse.class).thenCompose(playerResponse -> {
            if (playerResponse == null) {
                UUID uuid = Services.Mojang.getUUID(username).join();
                return getPlayer(uuid.toString());
            }
            SequoiaMod.debug("Fetched player data for username: " + username);
            return CompletableFuture.completedFuture(playerResponse);
        });
    }

    public CompletableFuture<PlayerResponse> getPlayerFullResult(String username) {
        String url = String.format(FULL_RESULT_URL, username);
        return HttpClients.WYNNCRAFT_API.getJsonAsync(url, PlayerResponse.class).thenCompose(playerResponse -> {
            if (playerResponse == null) {
                UUID uuid = Services.Mojang.getUUID(username).join();
                return getPlayerFullResult(uuid.toString());
            }
            SequoiaMod.debug("Fetched full player data for username: " + username);
            return CompletableFuture.completedFuture(playerResponse);
        });
    }
}
