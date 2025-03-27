/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.handler.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.ws.handler.WSMessageHandler;
import dev.lotnest.sequoia.core.ws.message.ws.ic3.SIC3DataWSMessage;

public class SIC3DataWSMessageHandler extends WSMessageHandler {
    public SIC3DataWSMessageHandler(String message) {
        super(GSON.fromJson(message, SIC3DataWSMessage.class), message);
    }

    @Override
    public void handle() {
        SIC3DataWSMessage sic3DataWSMessage = GSON.fromJson(message, SIC3DataWSMessage.class);
        SIC3DataWSMessage.Data sic3DataWSMessageData = sic3DataWSMessage.getSIC3Data();

        // TODO: Handle SIC3 data
        SequoiaMod.debug("Received SIC3 data: " + sic3DataWSMessageData);
    }
}
