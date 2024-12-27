package dev.lotnest.sequoia.feature.features.lootpool;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GLootPoolWSMessage extends WSMessage {
    public GLootPoolWSMessage(Data data) {
        super(WSMessageType.GLootPool.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(data));
    }

    public record Data(
            @SerializedName("loot_camp_name") String lootCampName,
            @SerializedName("loot_pool_items") List<LootPoolItem> lootPoolItems) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            return new EqualsBuilder()
                    .append(lootCampName, data.lootCampName)
                    .append(lootPoolItems, data.lootPoolItems)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(lootCampName)
                    .append(lootPoolItems)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("lootCampName", lootCampName)
                    .append("lootPoolItems", lootPoolItems)
                    .toString();
        }
    }
}
