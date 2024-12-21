package dev.lotnest.sequoia.feature.features.guildraidtracker;

import static dev.lotnest.sequoia.ws.SequoiaWebSocketClient.GSON;

import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class GuildRaidWSMessage extends WSMessage {
    public GuildRaidWSMessage(GuildRaid guildRaid) {
        super(WSMessageType.GRaidSubmission.getValue(), GSON.toJsonTree(guildRaid));
    }
}
