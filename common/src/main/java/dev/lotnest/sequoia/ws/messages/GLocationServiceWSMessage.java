package dev.lotnest.sequoia.ws.messages;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import dev.lotnest.sequoia.wynn.Location;
import java.util.Map;

public class GLocationServiceWSMessage extends WSMessage {
    public GLocationServiceWSMessage(Data data) {
        super(WSMessageType.G_LOCATION_SERVICE.getValue(), GSON.toJsonTree(data));
    }

    public Data getGLocationServiceData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(Map<String, Location> locations) {}
}
