/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.lotnest.sequoia.utils.wynn.api.item.ItemResponse.Identification;
import java.lang.reflect.Type;
import java.util.Map;

public class ItemResponseIdentificationAdapter
        implements JsonSerializer<Map<String, Identification>>, JsonDeserializer<Map<String, Identification>> {
    @Override
    public JsonElement serialize(Map<String, Identification> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        src.forEach((key, value) -> jsonObject.add(key, context.serialize(value)));
        return jsonObject;
    }

    /**
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context The deserialization context
     * @return The deserialized map
     * @throws JsonParseException If the JSON data cannot be parsed
     */
    @Override
    public Map<String, Identification> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, Identification> map = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement valueElement = entry.getValue();

            if (valueElement.isJsonObject()) {
                map.put(key, context.deserialize(valueElement, Identification.class));
            } else if (valueElement.isJsonPrimitive()
                    && valueElement.getAsJsonPrimitive().isNumber()) {
                map.put(key, new Identification(valueElement.getAsInt(), null, null));
            } else if (valueElement.isJsonPrimitive()
                    && valueElement.getAsJsonPrimitive().isString()) {
                try {
                    int intValue = Integer.parseInt(valueElement.getAsString());
                    map.put(key, new Identification(intValue, null, null));
                } catch (NumberFormatException ignored) {
                    throw new JsonParseException(
                            "Failed to parse value for key '" + key + "' as integer: " + valueElement.getAsString());
                }
            } else {
                throw new JsonParseException("Unexpected value type for key: " + key);
            }
        }

        return map;
    }
}
