/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.handlers;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.websocket.WSMessageHandler;
import dev.lotnest.sequoia.core.websocket.messages.SCommandPipeWSMessage;
import org.apache.commons.lang3.StringUtils;

public class SCommandPipeHandler extends WSMessageHandler {
    public SCommandPipeHandler(String message) {
        super(GSON.fromJson(message, SCommandPipeWSMessage.class), message);
    }

    @Override
    public void handle() {
        if (StringUtils.equals("Invalid token", wsMessage.getData().getAsString())) {
            SequoiaMod.debug("Received invalid token response. Requesting a new token.");
            SequoiaMod.getWebSocketFeature().authenticate(true);
        }
    }
}
