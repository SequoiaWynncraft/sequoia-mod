/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.ws.messages.session;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import java.time.OffsetDateTime;

public class SSessionResultWSMessage extends WSMessage {
    public SSessionResultWSMessage(Data data) {
        super(WSMessageType.SSessionResult.getValue(), GSON.toJsonTree(data));
    }

    public Data getSSessionResultData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(boolean error, String result, @SerializedName("expire_at") OffsetDateTime expireAt) {}
}
