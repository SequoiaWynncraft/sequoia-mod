/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.http;

import com.google.common.net.MediaType;
import dev.lotnest.sequoia.SequoiaMod;
import java.net.URI;
import java.net.http.HttpRequest;

public final class HttpUtils {
    public static final String USER_AGENT = SequoiaMod.MOD_ID + "-mod/" + SequoiaMod.getVersion()
            + "(minecraft:Lotnest; discord:@lotnest; github:Lotnest; mailto:lotnestyt@gmail.com; restrictions:no-reply-not-allowed)";

    private HttpUtils() {}

    public static HttpRequest newGetRequest(String url) {
        return HttpRequest.newBuilder()
                .header("User-Agent", USER_AGENT)
                .uri(URI.create(url))
                .GET()
                .build();
    }

    public static HttpRequest newPostRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", MediaType.JSON_UTF_8.type())
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}
