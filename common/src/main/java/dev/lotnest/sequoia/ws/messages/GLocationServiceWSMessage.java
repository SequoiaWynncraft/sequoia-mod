package dev.lotnest.sequoia.ws.messages;

import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import dev.lotnest.sequoia.wynn.Location;
import java.util.Map;

public class GLocationServiceWSMessage extends WSMessage {
    public GLocationServiceWSMessage(Data data) {
        super(WSMessageType.GLocationService.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(data));
    }

    public Data getGLocationServiceData() {
        return SequoiaWebSocketClient.GSON.fromJson(getData(), Data.class);
    }

    public record Data(Map<String, Location> locations) {}
}
