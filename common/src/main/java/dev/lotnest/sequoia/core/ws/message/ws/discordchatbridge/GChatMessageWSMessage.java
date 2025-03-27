/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.ws.discordchatbridge;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.core.ws.message.WSMessage;
import dev.lotnest.sequoia.core.ws.type.WSMessageType;

public class GChatMessageWSMessage extends WSMessage {
    public GChatMessageWSMessage(Data data) {
        super(WSMessageType.G_CHAT_MESSAGE.getValue(), GSON.toJsonTree(data));
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
