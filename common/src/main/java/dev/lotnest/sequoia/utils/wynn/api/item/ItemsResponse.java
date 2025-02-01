/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.utils.wynn.api.item;

import java.util.Map;

public record ItemsResponse(Map<String, ItemResponse> items) {
    public static final ItemsResponse EMPTY = new ItemsResponse(Map.of());

    public ItemsResponse(Map<String, ItemResponse> items) {
        this.items = items;
    }
}
