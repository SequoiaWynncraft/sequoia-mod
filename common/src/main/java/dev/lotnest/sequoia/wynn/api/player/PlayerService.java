/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.wynn.api.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.minecraft.MojangService;
import dev.lotnest.sequoia.utils.HttpUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerService {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/player/%s";
    private static final String FULL_RESULT_URL = BASE_URL + "?fullResult";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder().create();

    private PlayerService() {
    }

    public static CompletableFuture<PlayerResponse> getPlayer(String username) {
        String url = String.format(BASE_URL, username);
        HttpRequest request = HttpUtils.newGetRequest(url);

        return HTTP_CLIENT
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 && response.body() != null) {
                        SequoiaMod.debug("Fetched player data: " + response.body());
                        return GSON.fromJson(response.body(), PlayerResponse.class);
                    } else if (response.statusCode() == 300) {
                        UUID uuid = MojangService.getUuid(username).join();
                        return getPlayer(uuid.toString()).join();
                    } else {
                        SequoiaMod.error("Failed to fetch player data: " + response.statusCode());
                        return null;
                    }
                });
    }

    public static CompletableFuture<PlayerResponse> getPlayerFullResult(String username) {
        String url = String.format(FULL_RESULT_URL, username);
        HttpRequest request = HttpUtils.newGetRequest(url);

        return HTTP_CLIENT
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 && response.body() != null) {
                        SequoiaMod.debug("Fetched player data: " + response.body());
                        return GSON.fromJson(response.body(), PlayerResponse.class);
                    } else if (response.statusCode() == 300) {
                        UUID uuid = MojangService.getUuid(username).join();
                        return getPlayerFullResult(uuid.toString()).join();
                    } else {
                        SequoiaMod.error("Failed to fetch player data: " + response.statusCode());
                        return null;
                    }
                });
    }
}
