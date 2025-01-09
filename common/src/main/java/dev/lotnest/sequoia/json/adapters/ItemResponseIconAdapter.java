/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.json.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.lotnest.sequoia.wynn.api.item.ItemResponse;
import java.lang.reflect.Type;
import java.util.Map;

public class ItemResponseIconAdapter implements JsonSerializer<ItemResponse.Icon>, JsonDeserializer<ItemResponse.Icon> {
    @Override
    public JsonElement serialize(ItemResponse.Icon icon, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        if (icon.value() instanceof Map<?, ?> mapValue) {
            JsonObject valueObject = new JsonObject();
            mapValue.forEach((key, value) -> valueObject.add(key.toString(), context.serialize(value)));
            jsonObject.add("value", valueObject);
        } else if (icon.value() instanceof String stringValue) {
            jsonObject.addProperty("value", stringValue);
        }
        jsonObject.addProperty("format", icon.format());
        return jsonObject;
    }

    /**
     * @param json The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return
     * @throws JsonParseException
     */
    @Override
    public ItemResponse.Icon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement valueElement = jsonObject.get("value");
        Object value;

        if (valueElement.isJsonObject()) {
            value = context.deserialize(valueElement, Map.class);
        } else if (valueElement.isJsonPrimitive()
                && valueElement.getAsJsonPrimitive().isString()) {
            value = valueElement.getAsString();
        } else {
            throw new JsonParseException("Unexpected value type for Icon: " + valueElement);
        }

        String format = jsonObject.has("format") ? jsonObject.get("format").getAsString() : null;
        return new ItemResponse.Icon(value, format);
    }
}
