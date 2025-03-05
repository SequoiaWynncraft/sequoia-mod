/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import blue.endless.jankson.annotation.SerializedName;
import dev.lotnest.sequoia.core.ws.message.IStateOpCode;
import dev.lotnest.sequoia.core.ws.message.WSMessage;
import dev.lotnest.sequoia.core.ws.type.WSMessageType;

public class GIStateUpdateWSMessage extends WSMessage {
    public GIStateUpdateWSMessage(Data data) {
        super(WSMessageType.G_I_STATE_UPDATE.getValue(), GSON.toJsonTree(data));
    }

    public record Data(@SerializedName("state_data") IStateOpCode iStateOpCode) {}
}
