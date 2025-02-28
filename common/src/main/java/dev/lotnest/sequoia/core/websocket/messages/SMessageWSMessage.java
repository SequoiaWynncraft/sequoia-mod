/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.messages;

import com.google.gson.JsonElement;
import dev.lotnest.sequoia.core.websocket.WSMessage;
import dev.lotnest.sequoia.core.websocket.type.WSMessageType;

public class SMessageWSMessage extends WSMessage {
    public SMessageWSMessage(JsonElement data) {
        super(WSMessageType.S_MESSAGE.getValue(), data);
    }
}
