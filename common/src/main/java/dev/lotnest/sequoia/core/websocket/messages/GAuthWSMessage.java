/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.messages;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.core.websocket.WSMessage;
import dev.lotnest.sequoia.core.websocket.WSMessageType;

public class GAuthWSMessage extends WSMessage {
    public GAuthWSMessage(String data) {
        super(WSMessageType.G_AUTH.getValue(), GSON.toJsonTree(data));
    }

    public String getCode() {
        return GSON.fromJson(getData(), String.class);
    }
}
