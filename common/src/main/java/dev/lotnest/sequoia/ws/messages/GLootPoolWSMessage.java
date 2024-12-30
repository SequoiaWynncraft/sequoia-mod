package dev.lotnest.sequoia.ws.messages;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.feature.features.lootpool.LootPoolItem;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import java.util.List;

public class GLootPoolWSMessage extends WSMessage {
    public GLootPoolWSMessage(Data data) {
        super(WSMessageType.GLootPool.getValue(), GSON.toJsonTree(data));
    }

    public record Data(
            @SerializedName("loot_camp_name") String lootCampName,
            @SerializedName("loot_pool_items") List<LootPoolItem> lootPoolItems) {}
}
