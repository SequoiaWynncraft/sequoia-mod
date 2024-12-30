package dev.lotnest.sequoia.ws.messages.discordchatbridge;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class GChatMessageWSMessage extends WSMessage {
    public GChatMessageWSMessage(Data data) {
        super(WSMessageType.GChatMessage.getValue(), GSON.toJsonTree(data));
    }

    public Data getChatMessage() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            String username,
            String nickname,
            String message,
            String timestamp,
            @SerializedName("client_name") String clientName) {}
}
