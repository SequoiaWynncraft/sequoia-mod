/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.handlers;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.websocket.WSMessageHandler;
import dev.lotnest.sequoia.core.websocket.messages.ic3.SIC3DataWSMessage;

public class SIC3DataHandler extends WSMessageHandler {
    public SIC3DataHandler(String message) {
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
