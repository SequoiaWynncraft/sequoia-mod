/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.handlers;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import com.wynntils.core.WynntilsMod;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.events.WarPartyCreatedEvent;
import dev.lotnest.sequoia.core.events.WarPartyDisbandEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateRequestEvent;
import dev.lotnest.sequoia.core.websocket.WSMessageHandler;
import dev.lotnest.sequoia.core.websocket.messages.ic3.SIC3DataWSMessage;
import dev.lotnest.sequoia.models.war.WarPartyModel;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.chat.Component;

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

        switch (sic3DataWSMessageData.opCode()) {
            case 0 -> McUtils.sendMessageToClient(SequoiaMod.prefix(
                    Component.literal(new String(sic3DataWSMessageData.payload(), StandardCharsets.UTF_8))));
            case 1 -> {
                String payload = new String(sic3DataWSMessageData.payload(), StandardCharsets.UTF_8);
                switch (sic3DataWSMessageData.method()) {
                    case "partyCreated" -> {
                        String[] messages = payload.split(";");
                        WynntilsMod.postEvent(new WarPartyCreatedEvent(
                                Integer.parseInt(messages[0]),
                                messages[1],
                                messages[2],
                                WarPartyModel.Role.fromString(messages[3])));
                    }
                    case "partyDisbanded" -> WynntilsMod.postEvent(new WarPartyDisbandEvent(Integer.parseInt(payload)));
                    case "partyUpdate" -> {
                        String[] messages = payload.split(";");
                        WynntilsMod.postEvent(new WarPartyUpdateEvent(
                                Integer.parseInt(messages[0]),
                                messages[1],
                                Integer.parseInt(messages[2]),
                                WarPartyModel.Role.fromString(messages[3])));
                    }
                    case "updateRequest" -> WynntilsMod.postEvent(new WarPartyUpdateRequestEvent(payload));
                }
            }
        }
    }
}
