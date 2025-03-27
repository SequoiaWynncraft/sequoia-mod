/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.handler.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.ws.handler.WSMessageHandler;
import dev.lotnest.sequoia.core.ws.message.ws.binary.SBinaryDataWSMessage;

public class SBinaryDataWSMessageHandler extends WSMessageHandler {
    public SBinaryDataWSMessageHandler(String message) {
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
