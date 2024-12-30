package dev.lotnest.sequoia.wynn.api.guild;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.wynn.api.player.PlayerService;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public final class GuildService {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/guild/%s";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder().create();

    private static Boolean isSequoiaGuildMember = null;

    private GuildService() {
    }

    public static CompletableFuture<GuildResponse> getGuild(String guildName) {
        String normalUrl = String.format(BASE_URL, URLUtils.sanitize(guildName));
        String prefixUrl = String.format(BASE_URL, "prefix/" + URLUtils.sanitize(guildName));

        HttpRequest normalRequest =
                HttpRequest.newBuilder().uri(URI.create(normalUrl)).GET().build();
        HttpRequest prefixRequest =
                HttpRequest.newBuilder().uri(URI.create(prefixUrl)).GET().build();

        return HTTP_CLIENT
                .sendAsync(normalRequest, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    if (response.statusCode() == 200 && response.body() != null) {
                        SequoiaMod.debug("Fetched guild data: " + response.body());
                        return CompletableFuture.completedFuture(GSON.fromJson(response.body(), GuildResponse.class));
                    } else {
                        return HTTP_CLIENT
                                .sendAsync(prefixRequest, HttpResponse.BodyHandlers.ofString())
                                .thenApply(prefixResponse -> {
                                    if (prefixResponse.statusCode() == 200 && prefixResponse.body() != null) {
                                        SequoiaMod.debug("Fetched guild data with prefix: " + prefixResponse.body());
                                        return GSON.fromJson(prefixResponse.body(), GuildResponse.class);
                                    } else {
                                        SequoiaMod.error("Failed to fetch guild data: " + prefixResponse.statusCode());
                                        return null;
                                    }
                                });
                    }
                });
    }

    public static boolean isSequoiaGuildMember() {
        if (isSequoiaGuildMember == null) {
            PlayerResponse.Guild playerGuild =
                    PlayerService.getPlayer(McUtils.playerName()).join().getGuild();
            if (playerGuild == null) {
                isSequoiaGuildMember = false;
            } else {
                isSequoiaGuildMember =
                        StringUtils.equals(playerGuild.getUuid(), "ee860b7c-9a1d-49cf-9f19-ab673ba0f23b");
            }
        }
        return isSequoiaGuildMember;
    }
}
