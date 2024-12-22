package dev.lotnest.sequoia.ws.messages;

import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class SCommandPipeWSMessage extends WSMessage {
    public SCommandPipeWSMessage(String data) {
        super(WSMessageType.SCommandPipe.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(data));
    }
}
