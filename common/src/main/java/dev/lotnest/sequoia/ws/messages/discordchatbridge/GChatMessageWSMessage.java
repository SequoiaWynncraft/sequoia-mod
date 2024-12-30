package dev.lotnest.sequoia.ws.messages.discordchatbridge;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

public class GChatMessageWSMessage extends WSMessage {
    public GChatMessageWSMessage(Data data) {
        super(WSMessageType.GChatMessage.getValue(), GSON.toJsonTree(data));
    }

    public Data getChatMessage() {
        return SequoiaWebSocketClient.GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            String username,
            String nickname,
            String message,
            String timestamp,
            @SerializedName("client_name") String clientName) {
    }
}
