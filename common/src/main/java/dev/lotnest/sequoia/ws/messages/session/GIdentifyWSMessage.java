package dev.lotnest.sequoia.ws.messages.session;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class GIdentifyWSMessage extends WSMessage {
    public GIdentifyWSMessage(Data data) {
        super(WSMessageType.GIdentify.getValue(), GSON.toJsonTree(data));
    }

    public Data getGIdentifyData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            @SerializedName("access_token") String accessToken,
            String uuid,
            @SerializedName("mod_version") String modVersion) {}
}
