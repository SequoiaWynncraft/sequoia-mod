package dev.lotnest.sequoia.ws;

public enum WSMessageType {
    Invalid(0),

    GChatMessage(1),
    GClientCommand(2),
    GRaidSubmission(3),
    GLocationService(4),
    GGuildMap(5),
    GGetSessionID(6),
    GGuildWarResults(7),
    GLootPoolUpdate(8),
    GReserved7(9),
    GReserved8(10),
    GReserved9(11),
    GReserved10(12),

    SChannelMessage(13),
    SCommandData(14),
    SCommandResult(15),
    SChatMessageBroadcast(16),
    SCommandPipe(17),
    SRaidSubmission(18),
    SReserved2(19),
    SSessionIDResult(20),
    SReserved4(21),
    SReserved5(22),
    SReserved6(23),
    SReserved7(24),
    SReserved8(25),
    SReserved9(26),
    SReserved10(27),

    DChannelMessage(28);

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
