/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.ws.messages.session;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class GIdentifyWSMessage extends WSMessage {
    public GIdentifyWSMessage(Data data) {
        super(WSMessageType.G_IDENTIFY.getValue(), GSON.toJsonTree(data));
    }

    public Data getGIdentifyData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            @SerializedName("access_token") String accessToken,
            String uuid,
            @SerializedName("mod_version") int modVersion) {}
}
