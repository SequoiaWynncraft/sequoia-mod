/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.ws.session;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import dev.lotnest.sequoia.core.ws.message.WSMessage;
import dev.lotnest.sequoia.core.ws.type.WSMessageType;

public class GAuthWSMessage extends WSMessage {
    public GAuthWSMessage(String data) {
        super(WSMessageType.G_AUTH.getValue(), GSON.toJsonTree(data));
    }

    public String getCode() {
        return GSON.fromJson(getData(), String.class);
    }
}
