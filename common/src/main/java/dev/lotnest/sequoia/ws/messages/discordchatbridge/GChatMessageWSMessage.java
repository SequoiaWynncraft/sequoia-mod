package dev.lotnest.sequoia.ws.messages.discordchatbridge;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GChatMessageWSMessage extends WSMessage {
    public GChatMessageWSMessage(Data data) {
        super(WSMessageType.GChatMessage.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(data));
    }

    public Data getChatMessage() {
        return SequoiaWebSocketClient.GSON.fromJson(getData(), Data.class);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("data", getData()).toString();
    }

    public record Data(
            String username,
            String nickname,
            String message,
            String timestamp,
            @SerializedName("client_name") String clientName) {}
}
