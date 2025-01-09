/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.ws.handlers;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.ws.WSMessageHandler;
import dev.lotnest.sequoia.ws.messages.SCommandPipeWSMessage;
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
