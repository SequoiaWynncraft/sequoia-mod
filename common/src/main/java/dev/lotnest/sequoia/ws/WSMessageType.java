/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.ws;

public enum WSMessageType {
    Invalid(0),

    GChatMessage(1),
    GClientCommand(2),
    GRaidSubmission(3),
    GLocationService(4),
    GGuildMap(5),
    GIdentify(6),
    GGuildWarResults(7),
    GLootPool(8),
    GReserved5(9),
    GReserved6(10),
    GReserved7(11),
    GReserved8(12),
    GReserved9(13),
    GReserved10(14),

    SChannelMessage(15),
    SCommandData(16),
    SCommandResult(17),
    SChatMessageBroadcast(18),
    SCommandPipe(19),
    SRaidSubmission(20),
    SMessage(21),
    SSessionResult(22),
    SReserved5(23),
    SReserved6(24),
    SReserved7(25),
    SReserved8(26),
    SReserved9(27),
    SReserved10(28),

    DChannelMessage(29),
    DGetConnectedClient(30),
    DServerRestart(31);

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
        return Invalid;
    }

    public int getValue() {
        return value;
    }
}
