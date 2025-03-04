/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.http.clients;

import dev.lotnest.sequoia.core.http.HttpClient;
import dev.lotnest.sequoia.core.http.ratelimiter.RateLimiters;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class WynncraftApiHttpClient extends HttpClient {
    private WynncraftApiHttpClient() {
        super();
    }

    public static WynncraftApiHttpClient newHttpClient() {
        return new WynncraftApiHttpClient();
    }

    @Override
    public HttpResponse<String> get(String url) {
        RateLimiters.WYNNCRAFT_API.acquire();
        return super.get(url);
    }

    @Override
    public CompletableFuture<HttpResponse<String>> getAsync(String url) {
        return CompletableFuture.runAsync(RateLimiters.WYNNCRAFT_API::acquire).thenCompose(v -> super.getAsync(url));
    }

    @Override
    public HttpResponse<String> post(String url, String body) {
        RateLimiters.WYNNCRAFT_API.acquire();
        return super.post(url, body);
    }

    @Override
    public CompletableFuture<HttpResponse<String>> postAsync(String url, String body) {
        return CompletableFuture.runAsync(RateLimiters.WYNNCRAFT_API::acquire)
                .thenCompose(v -> super.postAsync(url, body));
    }
}
