package dev.lotnest.sequoia.ws.handlers;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.manager.managers.AccessTokenManager;
import dev.lotnest.sequoia.ws.WSMessageHandler;
import dev.lotnest.sequoia.ws.messages.session.SSessionResultWSMessage;
import org.apache.commons.lang3.StringUtils;

public class SSessionResultHandler extends WSMessageHandler {
    public SSessionResultHandler(String message) {
        super(GSON.fromJson(message, SSessionResultWSMessage.class), message);
    }

    @Override
    public void handle() {
        SSessionResultWSMessage sSessionResultWSMessage = GSON.fromJson(message, SSessionResultWSMessage.class);
        SSessionResultWSMessage.Data sSessionResultWSMessageData = sSessionResultWSMessage.getSessionResultData();

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
