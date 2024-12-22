package dev.lotnest.sequoia.ws.handlers;

import static dev.lotnest.sequoia.ws.SequoiaWebSocketClient.GSON;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.ws.WSMessageHandler;
import dev.lotnest.sequoia.ws.messages.SCommandPipeWSMessage;
import org.apache.commons.lang3.StringUtils;

public class SCommandPipeHandler extends WSMessageHandler {
    public SCommandPipeHandler(String message) {
        super(GSON.fromJson(message, SCommandPipeWSMessage.class), message);
    }

    @Override
    public void handle() {
        if (StringUtils.equals("Invalid token", wsMessage.getData().getAsString())) {
            SequoiaMod.debug("Received invalid token response. Requesting a new token.");
            SequoiaMod.getWebSocketClient().authenticate(true);
        }
    }
}
