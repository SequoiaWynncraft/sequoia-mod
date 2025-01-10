package dev.lotnest.sequoia.ws.messages;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class SCommandPipeWSMessage extends WSMessage {
    public SCommandPipeWSMessage(String data) {
        super(WSMessageType.S_COMMAND_PIPE.getValue(), GSON.toJsonTree(data));
    }
}
