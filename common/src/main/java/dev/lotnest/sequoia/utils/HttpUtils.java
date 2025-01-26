/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.utils;

import dev.lotnest.sequoia.SequoiaMod;
import java.net.URI;
import java.net.http.HttpRequest;

public final class HttpUtils {
    private HttpUtils() {}

    public static HttpRequest newGetRequest(String url) {
        return HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .header(
                        "User-Agent",
                        SequoiaMod.MOD_ID + "-mod/" + SequoiaMod.getVersion()
                                + "(minecraft:Lotnest; discord:@lotnest; github:Lotnest; mailto:lotnestyt@gmail.com; restrictions:no-reply-not-allowed)")
                .uri(URI.create(url))
                .GET()
                .build();
    }
}
