/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.handler.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.ws.handler.WSMessageHandler;
import dev.lotnest.sequoia.core.ws.message.ws.session.SSessionResultWSMessage;
import dev.lotnest.sequoia.managers.AccessTokenManager;
import org.apache.commons.lang3.StringUtils;

public class SSessionResultWSMessageHandler extends WSMessageHandler {
    public SSessionResultWSMessageHandler(String message) {
        super(GSON.fromJson(message, SSessionResultWSMessage.class), message);
    }

    @Override
    public void handle() {
        SSessionResultWSMessage sSessionResultWSMessage = GSON.fromJson(message, SSessionResultWSMessage.class);
        SSessionResultWSMessage.Data sSessionResultWSMessageData = sSessionResultWSMessage.getSSessionResultData();

        if (StringUtils.equals(sSessionResultWSMessageData.result(), "Authentication pending.")) {
            SequoiaMod.getWebSocketFeature().setAuthenticating(true);
            SequoiaMod.getWebSocketFeature().setAuthenticated(false);
            SequoiaMod.debug("Authentication pending, waiting for successful authentication.");
            return;
        }

        if (!sSessionResultWSMessageData.error()) {
            SequoiaMod.getWebSocketFeature().setAuthenticating(false);
            SequoiaMod.getWebSocketFeature().setAuthenticated(true);
            SequoiaMod.debug("Authenticated with WebSocket server.");

            if (!StringUtils.equals(AccessTokenManager.retrieveAccessToken(), sSessionResultWSMessageData.result())) {
                AccessTokenManager.storeAccessToken(sSessionResultWSMessageData.result());
            }
        } else {
            SequoiaMod.error("Failed to authenticate with WebSocket server: " + sSessionResultWSMessageData.result());
        }
    }
}
