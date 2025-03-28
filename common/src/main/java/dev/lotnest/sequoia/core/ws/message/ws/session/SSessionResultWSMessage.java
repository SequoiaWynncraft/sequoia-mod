/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.ws.session;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.core.ws.message.WSMessage;
import dev.lotnest.sequoia.core.ws.type.WSMessageType;
import java.time.OffsetDateTime;

public class SSessionResultWSMessage extends WSMessage {
    public SSessionResultWSMessage(Data data) {
        super(WSMessageType.S_SESSION_RESULT.getValue(), GSON.toJsonTree(data));
    }

    public Data getSSessionResultData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(boolean error, String result, @SerializedName("expire_at") OffsetDateTime expireAt) {}
}
