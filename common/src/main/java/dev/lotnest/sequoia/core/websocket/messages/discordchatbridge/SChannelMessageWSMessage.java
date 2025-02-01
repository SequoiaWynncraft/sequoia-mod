/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.messages.discordchatbridge;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.core.websocket.WSMessage;
import dev.lotnest.sequoia.core.websocket.WSMessageType;
import java.time.OffsetDateTime;

public class SChannelMessageWSMessage extends WSMessage {
    public SChannelMessageWSMessage(Data data) {
        super(WSMessageType.S_CHANNEL_MESSAGE.getValue(), GSON.toJsonTree(data));
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
            OffsetDateTime timestamp) {}
}
