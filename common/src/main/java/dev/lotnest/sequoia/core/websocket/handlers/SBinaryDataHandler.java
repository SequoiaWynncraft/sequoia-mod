/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.handlers;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.websocket.WSMessageHandler;
import dev.lotnest.sequoia.core.websocket.messages.binary.SBinaryDataWSMessage;

public class SBinaryDataHandler extends WSMessageHandler {
    public SBinaryDataHandler(String message) {
        super(GSON.fromJson(message, SBinaryDataWSMessage.class), message);
    }

    @Override
    public void handle() {
        SBinaryDataWSMessage sBinaryDataWSMessage = GSON.fromJson(message, SBinaryDataWSMessage.class);
        SBinaryDataWSMessage.Data sBinaryDataWSMessageData = sBinaryDataWSMessage.getSBinaryData();

        // TODO: Handle SBinary data
        SequoiaMod.debug("Received SBinaryData: " + sBinaryDataWSMessageData);
    }
}
