/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.handlers;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.websocket.WSMessageHandler;
import dev.lotnest.sequoia.core.websocket.messages.session.SSessionResultWSMessage;
import dev.lotnest.sequoia.managers.AccessTokenManager;
import org.apache.commons.lang3.StringUtils;

public class SSessionResultHandler extends WSMessageHandler {
    public SSessionResultHandler(String message) {
        super(GSON.fromJson(message, SSessionResultWSMessage.class), message);
    }

    @Override
    public void handle() {
        SSessionResultWSMessage sSessionResultWSMessage = GSON.fromJson(message, SSessionResultWSMessage.class);
        SSessionResultWSMessage.Data sSessionResultWSMessageData = sSessionResultWSMessage.getSSessionResultData();

        if (StringUtils.equals(sSessionResultWSMessageData.result(), "Authentication pending.")) {
            SequoiaMod.getWebSocketFeature().setAuthenticating(true);
            SequoiaMod.debug("Authentication pending, waiting for successful authentication.");
            return;
        }

        if (!sSessionResultWSMessageData.error()) {
            SequoiaMod.getWebSocketFeature().setAuthenticating(false);
            SequoiaMod.debug("Authenticated with WebSocket server.");

            if (!StringUtils.equals(AccessTokenManager.retrieveAccessToken(), sSessionResultWSMessageData.result())) {
                AccessTokenManager.storeAccessToken(sSessionResultWSMessageData.result());
            }
        } else {
            SequoiaMod.error("Failed to authenticate with WebSocket server: " + sSessionResultWSMessageData.result());
        }
    }
}
