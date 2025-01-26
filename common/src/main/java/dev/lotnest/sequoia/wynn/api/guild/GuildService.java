/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.wynn.api.guild;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.utils.URLUtils;
import java.util.concurrent.CompletableFuture;

public final class GuildService {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/guild/%s";

    private GuildService() {}

    public static CompletableFuture<GuildResponse> getGuild(String guildName) {
        String normalUrl = String.format(BASE_URL, URLUtils.sanitize(guildName));
        String prefixUrl = String.format(BASE_URL, "prefix/" + URLUtils.sanitize(guildName));

        CompletableFuture<GuildResponse> normalResponse =
                SequoiaMod.getHttpClient().getJsonAsync(normalUrl, GuildResponse.class);
        return normalResponse.thenCompose(response -> {
            if (response != null) {
                SequoiaMod.debug("Fetched guild data: " + response);
                return CompletableFuture.completedFuture(response);
            } else {
                return SequoiaMod.getHttpClient()
                        .getJsonAsync(prefixUrl, GuildResponse.class)
                        .thenApply(prefixResponse -> {
                            if (prefixResponse != null) {
                                SequoiaMod.debug("Fetched guild data with prefix: " + prefixResponse);
                                return prefixResponse;
                            } else {
                                SequoiaMod.error("Failed to fetch guild data");
                                return null;
                            }
                        });
            }
        });
    }
}
