package dev.lotnest.sequoia.wynn.api.item;

import java.util.Map;

public record ItemsResponse(Map<String, ItemResponse> items) {
    public static final ItemsResponse EMPTY = new ItemsResponse(Map.of());

    public ItemsResponse(Map<String, ItemResponse> items) {
        this.items = items;
    }
}
