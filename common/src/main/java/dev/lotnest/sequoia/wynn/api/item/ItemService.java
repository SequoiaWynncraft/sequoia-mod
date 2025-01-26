/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.wynn.api.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.json.adapters.ItemResponseIconAdapter;
import dev.lotnest.sequoia.json.adapters.ItemResponseIdentificationAdapter;
import dev.lotnest.sequoia.json.adapters.ItemsResponseAdapter;
import dev.lotnest.sequoia.json.typetokens.ItemsResponseTypeToken;
import dev.lotnest.sequoia.utils.HttpUtils;
import dev.lotnest.sequoia.utils.URLUtils;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public final class ItemService {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/item";
    private static final String SEARCH_URL = BASE_URL + "/search/%s";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ItemsResponse.class, new ItemsResponseAdapter())
            .registerTypeAdapter(ItemResponse.Icon.class, new ItemResponseIconAdapter())
            .registerTypeAdapter(ItemResponse.Identification.class, new ItemResponseIdentificationAdapter())
            .create();

    private ItemService() {}

    public static CompletableFuture<ItemsResponse> searchItem(String itemName) {
        String url = String.format(SEARCH_URL, URLUtils.sanitize(itemName));
        HttpRequest request = HttpUtils.newGetRequest(url);

        return HTTP_CLIENT
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 && response.body() != null) {
                        SequoiaMod.debug("Fetched item data: " + response.body());
                        try {
                            ItemsResponse result =
                                    GSON.fromJson(response.body(), new ItemsResponseTypeToken().getType());
                            SequoiaMod.debug("Parsed item data: " + result);
                            return result;
                        } catch (Exception exception) {
                            SequoiaMod.error("Failed to parse item data: " + exception.getMessage());
                            return ItemsResponse.EMPTY;
                        }
                    } else {
                        SequoiaMod.error("Failed to fetch item data: " + response.statusCode());
                        return ItemsResponse.EMPTY;
                    }
                })
                .exceptionally(throwable -> {
                    SequoiaMod.error("Failed to fetch item data: " + itemName, throwable);
                    return ItemsResponse.EMPTY;
                });
    }
}
