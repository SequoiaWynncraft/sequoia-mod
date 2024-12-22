package dev.lotnest.sequoia.ws.messages.session;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SSessionResultWSMessage extends WSMessage {
    public SSessionResultWSMessage(Data data) {
        super(WSMessageType.SSessionResult.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(data));
    }

    public Data getSessionResultData() {
        return SequoiaWebSocketClient.GSON.fromJson(getData(), Data.class);
    }

    public record Data(boolean error, String result, @SerializedName("expire_at") OffsetDateTime expireAt) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            return new EqualsBuilder()
                    .append(error, data.error)
                    .append(result, data.result)
                    .append(expireAt, data.expireAt)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(error)
                    .append(result)
                    .append(expireAt)
                    .toHashCode();
        }
    }
}
