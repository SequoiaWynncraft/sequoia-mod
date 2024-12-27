package dev.lotnest.sequoia.ws.messages.discordchatbridge;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SChannelMessageWSMessage extends WSMessage {
    public SChannelMessageWSMessage(Data data) {
        super(WSMessageType.SChannelMessage.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(data));
    }

    public Data getChannelMessageData() {
        return SequoiaWebSocketClient.GSON.fromJson(getData(), Data.class);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("data", getData()).toString();
    }

    public record Data(
            String username,
            String nickname,
            @SerializedName("display_name") String displayName,
            @SerializedName("sequoia_roles") String[] sequoiaRoles,
            String message,
            OffsetDateTime timestamp) {}
}
