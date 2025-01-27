/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.wynn.api.player;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.minecraft.MojangService;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerService {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/player/%s";
    private static final String FULL_RESULT_URL = BASE_URL + "?fullResult";

    private PlayerService() {}

    public static CompletableFuture<PlayerResponse> getPlayer(String username) {
        String url = String.format(BASE_URL, username);
        return SequoiaMod.getHttpClient()
                .getJsonAsync(url, PlayerResponse.class)
                .thenCompose(playerResponse -> {
                    if (playerResponse == null) {
                        UUID uuid = MojangService.getUuid(username).join();
                        return getPlayer(uuid.toString());
                    }
                    SequoiaMod.debug("Fetched player data for username: " + username);
                    return CompletableFuture.completedFuture(playerResponse);
                });
    }

    public static CompletableFuture<PlayerResponse> getPlayerFullResult(String username) {
        String url = String.format(FULL_RESULT_URL, username);
        return SequoiaMod.getHttpClient()
                .getJsonAsync(url, PlayerResponse.class)
                .thenCompose(playerResponse -> {
                    if (playerResponse == null) {
                        UUID uuid = MojangService.getUuid(username).join();
                        return getPlayerFullResult(uuid.toString());
                    }
                    SequoiaMod.debug("Fetched full player data for username: " + username);
                    return CompletableFuture.completedFuture(playerResponse);
                });
    }
}
