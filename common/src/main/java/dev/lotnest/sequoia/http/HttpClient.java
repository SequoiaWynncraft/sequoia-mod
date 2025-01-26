/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.http;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.SequoiaMod;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private static final Gson GSON = new GsonBuilder().create();
    private static final int[] OK_STATUS_CODES = {200, 201, 202, 203, 204, 205, 206, 207, 208, 226};

    private final Cache<String, HttpResponse<String>> responseCache;
    private final java.net.http.HttpClient httpClient;

    private HttpClient() {
        this(1, TimeUnit.MINUTES);
    }

    private HttpClient(long cacheDuration, TimeUnit cacheDurationUnit) {
        responseCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheDuration, cacheDurationUnit)
                .build();
        httpClient = java.net.http.HttpClient.newHttpClient();
    }

    public static HttpClient newHttpClient() {
        return new HttpClient();
    }

    public static HttpClient newHttpClient(long cacheDuration, TimeUnit cacheDurationUnit) {
        return new HttpClient(cacheDuration, cacheDurationUnit);
    }

    private boolean isOkStatusCode(int statusCode) {
        for (int okStatusCode : OK_STATUS_CODES) {
            if (statusCode == okStatusCode) {
                return true;
            }
        }
        return false;
    }

    public HttpResponse<String> get(String url) {
        try {
            return responseCache.get(url, () -> {
                SequoiaMod.debug("Fetching response from: " + url);
                java.net.http.HttpRequest request = HttpUtils.newGetRequest(url);
                return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            });
        } catch (ExecutionException exception) {
            SequoiaMod.error("Failed to fetch response", exception);
            return null;
        }
    }

    public CompletableFuture<HttpResponse<String>> getAsync(String url) {
        try {
            SequoiaMod.debug("Fetching async response from: " + url);
            java.net.http.HttpRequest request = HttpUtils.newGetRequest(url);
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception exception) {
            SequoiaMod.error("Failed to fetch response", exception);
            return null;
        }
    }

    public HttpResponse<String> post(String url, String body) {
        try {
            return responseCache.get(url, () -> {
                SequoiaMod.debug("Posting to: " + url + " with body: " + body);
                java.net.http.HttpRequest request = HttpUtils.newPostRequest(url, body);
                return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            });
        } catch (ExecutionException exception) {
            SequoiaMod.error("Failed to fetch response", exception);
            return null;
        }
    }

    public CompletableFuture<HttpResponse<String>> postAsync(String url, String body) {
        try {
            SequoiaMod.debug("Posting async to: " + url + " with body: " + body);
            java.net.http.HttpRequest request = HttpUtils.newPostRequest(url, body);
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception exception) {
            SequoiaMod.error("Failed to fetch response", exception);
            return null;
        }
    }

    public <T> T getJson(String url, Class<T> responseType) {
        return getJson(url, responseType, GSON);
    }

    public <T> T getJson(String url, Class<T> responseType, Gson gson) {
        HttpResponse<String> response = get(url);

        SequoiaMod.debug(response.statusCode() + " " + url + " " + response.body());

        if (response != null && response.body() != null && isOkStatusCode(response.statusCode())) {
            return gson.fromJson(response.body(), responseType);
        }
        return null;
    }

    public <T> CompletableFuture<T> getJsonAsync(String url, Class<T> responseType) {
        return getJsonAsync(url, responseType, GSON);
    }

    public <T> CompletableFuture<T> getJsonAsync(String url, Class<T> responseType, Gson gson) {
        return getAsync(url).thenApply(response -> {
            SequoiaMod.debug("ASYNC " + response.statusCode() + " " + url + " " + response.body());

            if (response != null && response.body() != null && isOkStatusCode(response.statusCode())) {
                return gson.fromJson(response.body(), responseType);
            }

            return null;
        });
    }
}
