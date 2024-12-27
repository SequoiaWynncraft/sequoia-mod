package dev.lotnest.sequoia.ws.messages;

import dev.lotnest.sequoia.feature.features.guildraidtracker.GuildRaid;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;

public class GuildRaidWSMessage extends WSMessage {
    public GuildRaidWSMessage(GuildRaid guildRaid) {
        super(WSMessageType.GRaidSubmission.getValue(), SequoiaWebSocketClient.GSON.toJsonTree(guildRaid));
    }
}
