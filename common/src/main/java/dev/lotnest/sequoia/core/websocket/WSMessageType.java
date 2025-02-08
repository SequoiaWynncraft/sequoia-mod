/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket;

public enum WSMessageType {
    INVALID,

    G_CHAT_MESSAGE,
    G_CLIENT_COMMAND,
    G_RAID_SUBMISSION,
    G_LOCATION_SERVICE,
    G_GUILD_MAP,
    G_IDENTIFY,
    G_GUILD_WAR_RESULTS,
    G_LOOT_POOL,
    G_AUTH,
    G_IC3H,
    G_RESERVED_7,
    G_RESERVED_8,
    G_RESERVED_9,
    G_RESERVED_10,

    S_CHANNEL_MESSAGE,
    S_COMMAND_DATA,
    S_COMMAND_RESULT,
    S_CHAT_MESSAGE_BROADCAST,
    S_COMMAND_PIPE,
    S_RAID_SUBMISSION,
    S_MESSAGE,
    S_SESSION_RESULT,
    S_IC3_DATA,
    S_RESERVED_6,
    S_RESERVED_7,
    S_RESERVED_8,
    S_RESERVED_9,
    S_RESERVED_10,

    D_CHANNEL_MESSAGE,
    D_GET_CONNECTED_CLIENT,
    D_SERVER_RESTART,
    D_SERVER_MESSAGE;

    public static WSMessageType fromValue(int value) {
        for (WSMessageType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return INVALID;
    }

    public int getValue() {
        return ordinal();
    }
}
