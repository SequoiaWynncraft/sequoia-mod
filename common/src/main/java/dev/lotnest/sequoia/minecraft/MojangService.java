/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.utils.HttpUtils;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class MojangService {
    private static final String USERS_PROFILES_MINECRAFT_BASE_URL =
            "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Pattern UNDASHED_UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    private MojangService() {}

    public static CompletableFuture<UUID> getUuid(String username) {
        String url = String.format(USERS_PROFILES_MINECRAFT_BASE_URL, username);
        HttpRequest request = HttpUtils.newGetRequest(url);

        return HTTP_CLIENT
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 && response.body() != null) {
                        try {
                            MojangUsersProfilesMinecraftResponse mojangUsersProfilesMinecraftResponse =
                                    GSON.fromJson(response.body(), MojangUsersProfilesMinecraftResponse.class);
                            return UUID.fromString(UNDASHED_UUID_PATTERN
                                    .matcher(mojangUsersProfilesMinecraftResponse.getId())
                                    .replaceFirst("$1-$2-$3-$4-$5"));
                        } catch (Exception exception) {
                            SequoiaMod.error("Failed to parse MojangUsersProfilesMinecraftResponse player data: "
                                    + exception.getMessage());
                            return null;
                        }
                    } else {
                        SequoiaMod.error("Failed to fetch Mojang player data: " + response.statusCode());
                        return null;
                    }
                });
    }
}
