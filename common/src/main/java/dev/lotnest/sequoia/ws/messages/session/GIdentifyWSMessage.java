package dev.lotnest.sequoia.ws.messages.session;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GIdentifyWSMessage extends WSMessage {
    public GIdentifyWSMessage(Data data) {
        super(WSMessageType.GIdentify.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(data));
    }

    public Data getIdentifyData() {
        return SequoiaWebSocketClient.GSON.fromJson(getData(), Data.class);
    }

    public record Data(@SerializedName("access_token") String accessToken, String uuid) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            return new EqualsBuilder()
                    .append(accessToken, data.accessToken)
                    .append(uuid, data.uuid)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(accessToken).append(uuid).toHashCode();
        }
    }
}
