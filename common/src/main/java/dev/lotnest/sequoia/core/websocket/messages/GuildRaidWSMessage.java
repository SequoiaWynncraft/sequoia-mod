/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.messages;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import dev.lotnest.sequoia.core.websocket.WSMessage;
import dev.lotnest.sequoia.core.websocket.type.WSMessageType;
import dev.lotnest.sequoia.features.guildraidtracker.GuildRaid;

public class GuildRaidWSMessage extends WSMessage {
    public GuildRaidWSMessage(GuildRaid guildRaid) {
        super(WSMessageType.G_RAID_SUBMISSION.getValue(), GSON.toJsonTree(guildRaid));
    }
}
