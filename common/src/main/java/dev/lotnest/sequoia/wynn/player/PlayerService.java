package dev.lotnest.sequoia.wynn.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.mojang.MojangService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerService {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/player/%s";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder().create();

    private PlayerService() {}

    public static CompletableFuture<Player> getPlayer(String username) {
        String url = String.format(BASE_URL, username);
        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        return HTTP_CLIENT
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 && response.body() != null) {
                        SequoiaMod.debug("Fetched player data: " + response.body());
                        return GSON.fromJson(response.body(), Player.class);
                    } else if (response.statusCode() == 300) {
                        UUID uuid = MojangService.getUuid(username).join();
                        return getPlayer(uuid.toString()).join();
                    } else {
                        SequoiaMod.error("Failed to fetch player data: " + response.statusCode());
                        return null;
                    }
                });
    }
}
