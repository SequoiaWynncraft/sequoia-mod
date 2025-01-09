/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.ws.messages;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class SCommandPipeWSMessage extends WSMessage {
    public SCommandPipeWSMessage(String data) {
        super(WSMessageType.SCommandPipe.getValue(), GSON.toJsonTree(data));
    }
}
