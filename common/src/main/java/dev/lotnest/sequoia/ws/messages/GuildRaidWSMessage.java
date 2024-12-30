package dev.lotnest.sequoia.ws.messages;

import static dev.lotnest.sequoia.feature.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.feature.features.guildraidtracker.GuildRaid;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class GuildRaidWSMessage extends WSMessage {
    public GuildRaidWSMessage(GuildRaid guildRaid) {
        super(WSMessageType.GRaidSubmission.getValue(), GSON.toJsonTree(guildRaid));
    }
}
