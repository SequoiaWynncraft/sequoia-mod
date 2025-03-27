/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import dev.lotnest.sequoia.core.ws.message.WSMessage;
import dev.lotnest.sequoia.core.ws.type.WSMessageType;

public class SCommandPipeWSMessage extends WSMessage {
    public SCommandPipeWSMessage(String data) {
        super(WSMessageType.S_COMMAND_PIPE.getValue(), GSON.toJsonTree(data));
    }
}
