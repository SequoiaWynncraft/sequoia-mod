package dev.lotnest.sequoia.ws.messages.session;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import java.time.OffsetDateTime;

public class SSessionResultWSMessage extends WSMessage {
    public SSessionResultWSMessage(Data data) {
        super(WSMessageType.SSessionResult.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(data));
    }

    public Data getSSessionResultData() {
        return SequoiaWebSocketClient.GSON.fromJson(getData(), Data.class);
    }

    public record Data(boolean error, String result, @SerializedName("expire_at") OffsetDateTime expireAt) {}
}
