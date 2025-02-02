/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.services.wynn.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Service;
import dev.lotnest.sequoia.core.json.ItemResponseIconAdapter;
import dev.lotnest.sequoia.core.json.ItemResponseIdentificationAdapter;
import dev.lotnest.sequoia.core.json.ItemsResponseAdapter;
import dev.lotnest.sequoia.utils.URLUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ItemService extends Service {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/item";
    private static final String SEARCH_URL = BASE_URL + "/search/%s";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ItemsResponse.class, new ItemsResponseAdapter())
            .registerTypeAdapter(ItemResponse.Icon.class, new ItemResponseIconAdapter())
            .registerTypeAdapter(ItemResponse.Identification.class, new ItemResponseIdentificationAdapter())
            .create();

    public ItemService() {
        super(List.of());
    }

    public CompletableFuture<ItemsResponse> searchItem(String itemName) {
        String url = String.format(SEARCH_URL, URLUtils.sanitize(itemName));
        return SequoiaMod.getHttpClient()
                .getJsonAsync(url, ItemsResponse.class, GSON)
                .thenApply(result -> {
                    if (result != null) {
                        SequoiaMod.debug("Fetched and parsed item data: " + result);
                        return result;
                    } else {
                        SequoiaMod.error("Failed to fetch or parse item data for: " + itemName);
                        return ItemsResponse.EMPTY;
                    }
                })
                .exceptionally(throwable -> {
                    SequoiaMod.error("Failed to fetch item data: " + itemName, throwable);
                    return ItemsResponse.EMPTY;
                });
    }
}
