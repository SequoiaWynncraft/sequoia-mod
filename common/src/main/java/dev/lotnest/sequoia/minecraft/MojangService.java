/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.minecraft;

import dev.lotnest.sequoia.SequoiaMod;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class MojangService {
    private static final String USERS_PROFILES_MINECRAFT_BASE_URL =
            "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final Pattern UNDASHED_UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    private MojangService() {}

    public static CompletableFuture<UUID> getUuid(String username) {
        String url = String.format(USERS_PROFILES_MINECRAFT_BASE_URL, username);
        return SequoiaMod.getHttpClient()
                .getJsonAsync(url, MojangUsersProfilesMinecraftResponse.class)
                .thenApply(response -> {
                    if (response != null) {
                        try {
                            return UUID.fromString(UNDASHED_UUID_PATTERN
                                    .matcher(response.getId())
                                    .replaceFirst("$1-$2-$3-$4-$5"));
                        } catch (Exception exception) {
                            SequoiaMod.error("Failed to parse UUID from player data: " + exception.getMessage());
                            return null;
                        }
                    } else {
                        SequoiaMod.error("Failed to fetch Mojang player data");
                        return null;
                    }
                });
    }
}
