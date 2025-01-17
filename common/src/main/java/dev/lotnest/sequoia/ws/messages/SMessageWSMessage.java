/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.ws.messages;

import com.google.gson.JsonElement;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class SMessageWSMessage extends WSMessage {
    public SMessageWSMessage(JsonElement data) {
        super(WSMessageType.S_MESSAGE.getValue(), data);
    }
}
