/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.messages;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.core.websocket.WSMessage;
import dev.lotnest.sequoia.core.websocket.WSMessageType;
import dev.lotnest.sequoia.utils.wynn.WynnLocation;
import java.util.Map;

public class GLocationServiceWSMessage extends WSMessage {
    public GLocationServiceWSMessage(Data data) {
        super(WSMessageType.G_LOCATION_SERVICE.getValue(), GSON.toJsonTree(data));
    }

    public Data getGLocationServiceData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(Map<String, WynnLocation> locations) {}
}
