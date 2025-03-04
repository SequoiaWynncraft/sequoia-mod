/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.handler.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.ws.handler.WSMessageHandler;
import dev.lotnest.sequoia.core.ws.message.ws.SCommandPipeWSMessage;
import org.apache.commons.lang3.StringUtils;

public class SCommandPipeWSMessageHandler extends WSMessageHandler {
    public SCommandPipeWSMessageHandler(String message) {
        super(GSON.fromJson(message, SCommandPipeWSMessage.class), message);
    }

    @Override
    public void handle() {
        if (StringUtils.equals("Invalid token", wsMessage.getData().getAsString())) {
            SequoiaMod.debug("Received invalid token response. Requesting a new token.");
            SequoiaMod.getWebSocketFeature().authenticate(true);
        } else if (StringUtils.equals("Authenticated.", wsMessage.getData().getAsString())) {
            SequoiaMod.debug("Authenticated with WebSocket server.");
            SequoiaMod.getWebSocketFeature().setAuthenticating(false);
            SequoiaMod.getWebSocketFeature().setAuthenticated(true);
        }
    }
}
