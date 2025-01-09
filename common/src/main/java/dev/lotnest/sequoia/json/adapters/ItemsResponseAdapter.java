/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.json.adapters;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.lotnest.sequoia.wynn.api.item.ItemResponse;
import dev.lotnest.sequoia.wynn.api.item.ItemsResponse;
import java.lang.reflect.Type;
import java.util.Map;

public class ItemsResponseAdapter implements JsonDeserializer<ItemsResponse>, JsonSerializer<ItemsResponse> {
    /**
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return
     * @throws JsonParseException
     */
    @Override
    public ItemsResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, ItemResponse> items = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            items.put(entry.getKey(), context.deserialize(entry.getValue(), ItemResponse.class));
        }

        return new ItemsResponse(items);
    }

    @Override
    public JsonElement serialize(ItemsResponse src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<String, ItemResponse> entry : src.items().entrySet()) {
            jsonObject.add(entry.getKey(), context.serialize(entry.getValue(), ItemResponse.class));
        }

        return jsonObject;
    }
}
