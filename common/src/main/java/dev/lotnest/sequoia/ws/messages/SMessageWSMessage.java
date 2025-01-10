package dev.lotnest.sequoia.ws.messages;

import com.google.gson.JsonElement;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class SMessageWSMessage extends WSMessage {
    public SMessageWSMessage(JsonElement data) {
        super(WSMessageType.S_MESSAGE.getValue(), data);
    }
}
