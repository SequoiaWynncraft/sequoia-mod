package dev.lotnest.sequoia.wynn.guild;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.SequoiaMod;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public final class GuildService {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/guild/%s";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder().create();

    private GuildService() {}

    public static CompletableFuture<Guild> getGuild(String guildName) {
        String url = String.format(BASE_URL, guildName);
        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        return httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 && response.body() != null) {
                        SequoiaMod.debug("Fetched guild data: " + response.body());
                        return gson.fromJson(response.body(), Guild.class);
                    } else {
                        SequoiaMod.error("Failed to fetch guild data: " + response.statusCode());
                        return null;
                    }
                });
    }
}
