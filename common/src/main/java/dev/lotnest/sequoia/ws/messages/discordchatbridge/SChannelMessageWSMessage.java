package dev.lotnest.sequoia.ws.messages.discordchatbridge;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

import java.time.OffsetDateTime;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

public class SChannelMessageWSMessage extends WSMessage {
    public SChannelMessageWSMessage(Data data) {
        super(WSMessageType.SChannelMessage.getValue(), GSON.toJsonTree(data));
    }

    public Data getSChannelMessageData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            String username,
            String nickname,
            @SerializedName("display_name") String displayName,
            @SerializedName("sequoia_roles") String[] sequoiaRoles,
            String message,
            OffsetDateTime timestamp) {
    }
}
