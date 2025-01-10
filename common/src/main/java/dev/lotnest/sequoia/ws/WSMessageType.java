/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.ws;

public enum WSMessageType {
    INVALID(0),

    G_CHAT_MESSAGE(1),
    G_CLIENT_COMMAND(2),
    G_RAID_SUBMISSION(3),
    G_LOCATION_SERVICE(4),
    G_GUILD_MAP(5),
    G_IDENTIFY(6),
    G_GUILD_WAR_RESULTS(7),
    G_LOOT_POOL(8),
    G_RESERVED_5(9),
    G_RESERVED_6(10),
    G_RESERVED_7(11),
    G_RESERVED_8(12),
    G_RESERVED_9(13),
    G_RESERVED_10(14),

    S_CHANNEL_MESSAGE(15),
    S_COMMAND_DATA(16),
    S_COMMAND_RESULT(17),
    S_CHAT_MESSAGE_BROADCAST(18),
    S_COMMAND_PIPE(19),
    S_RAID_SUBMISSION(20),
    S_MESSAGE(21),
    S_SESSION_RESULT(22),
    S_RESERVED_5(23),
    S_RESERVED_6(24),
    S_RESERVED_7(25),
    S_RESERVED_8(26),
    S_RESERVED_9(27),
    S_RESERVED_10(28),

    D_CHANNEL_MESSAGE(29),
    D_GET_CONNECTED_CLIENT(30),
    D_SERVER_RESTART(31);

    private final int value;

    WSMessageType(int value) {
        this.value = value;
    }

    public static WSMessageType fromValue(int value) {
        for (WSMessageType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return INVALID;
    }

    public int getValue() {
        return value;
    }
}
