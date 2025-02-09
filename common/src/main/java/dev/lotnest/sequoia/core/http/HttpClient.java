/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.http;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.SequoiaMod;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private static final int[] OK_STATUS_CODES = {200, 201, 202, 203, 204, 205, 206, 207, 208, 226};
    private static final int POOL_SIZE = 10;
    private static final Gson gson = new GsonBuilder().create();
    private static final ConcurrentMap<java.net.http.HttpClient, Boolean> httpClientPool = Maps.newConcurrentMap();

    private HttpClient() {
        this(1, TimeUnit.MINUTES);
    }

    private HttpClient(long cacheDuration, TimeUnit cacheDurationUnit) {
        createClientPool();
    }

    public static HttpClient newHttpClient() {
        return new HttpClient();
    }

    public static HttpClient newHttpClient(long cacheDuration, TimeUnit cacheDurationUnit) {
        return new HttpClient(cacheDuration, cacheDurationUnit);
    }

    private void createClientPool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            httpClientPool.put(client, false);
        }
    }

    private boolean isOkStatusCode(int statusCode) {
        for (int okStatusCode : OK_STATUS_CODES) {
            if (statusCode == okStatusCode) {
                return true;
            }
        }
        return false;
    }

    private java.net.http.HttpClient getAvailableClient() {
        for (Map.Entry<java.net.http.HttpClient, Boolean> entry : httpClientPool.entrySet()) {
            java.net.http.HttpClient client = entry.getKey();
            if (Boolean.FALSE.equals(entry.getValue())) {
                httpClientPool.put(client, true);
                return client;
            }
        }

        java.net.http.HttpClient newClient = java.net.http.HttpClient.newHttpClient();
        httpClientPool.put(newClient, true);
        return newClient;
    }

    public HttpResponse<String> get(String url) {
        java.net.http.HttpClient client = getAvailableClient();
        try {
            java.net.http.HttpRequest request = HttpUtils.newGetRequest(url);
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SequoiaMod.error("Thread interrupted while fetching response", e);
            return null;
        } catch (Exception exception) {
            SequoiaMod.error("Failed to fetch response", exception);
            return null;
        } finally {
            httpClientPool.put(client, false);
        }
    }

    public CompletableFuture<HttpResponse<String>> getAsync(String url) {
        try {
            java.net.http.HttpClient client = getAvailableClient();
            java.net.http.HttpRequest request = HttpUtils.newGetRequest(url);
            CompletableFuture<HttpResponse<String>> responseFuture =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            responseFuture.thenRun(() -> httpClientPool.put(client, false));

            return responseFuture;
        } catch (Exception exception) {
            SequoiaMod.error("Failed to fetch async response", exception);
            return null;
        }
    }

    public HttpResponse<String> post(String url, String body) {
        java.net.http.HttpClient client = getAvailableClient();
        try {
            java.net.http.HttpRequest request = HttpUtils.newPostRequest(url, body);
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SequoiaMod.error("Thread interrupted while posting response", e);
            return null;
        } catch (Exception exception) {
            SequoiaMod.error("Failed to post response", exception);
            return null;
        } finally {
            httpClientPool.put(client, false);
        }
    }

    public CompletableFuture<HttpResponse<String>> postAsync(String url, String body) {
        try {
            java.net.http.HttpClient client = getAvailableClient();
            java.net.http.HttpRequest request = HttpUtils.newPostRequest(url, body);
            CompletableFuture<HttpResponse<String>> responseFuture =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            responseFuture.thenRun(() -> httpClientPool.put(client, false));

            return responseFuture;
        } catch (Exception exception) {
            SequoiaMod.error("Failed to post async response", exception);
            return null;
        }
    }

    public <T> T getJson(String url, Class<T> responseType) {
        return getJson(url, responseType, gson);
    }

    public <T> T getJson(String url, Class<T> responseType, Gson gson) {
        HttpResponse<String> response = get(url);

        SequoiaMod.debug(response.statusCode() + " " + url + " " + response.body());

        if (response.body() != null && isOkStatusCode(response.statusCode())) {
            return gson.fromJson(response.body(), responseType);
        }
        return null;
    }

    public <T> CompletableFuture<T> getJsonAsync(String url, Class<T> responseType) {
        return getJsonAsync(url, responseType, gson);
    }

    public <T> CompletableFuture<T> getJsonAsync(String url, Class<T> responseType, Gson gson) {
        return getAsync(url).thenApply(response -> {
            SequoiaMod.debug("ASYNC " + response.statusCode() + " " + url + " " + response.body());

            if (response.body() != null && isOkStatusCode(response.statusCode())) {
                return gson.fromJson(response.body(), responseType);
            }

            return null;
        });
    }
}
