/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.istateopcodes;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import blue.endless.jankson.annotation.SerializedName;
import dev.lotnest.sequoia.core.ws.message.IStateOpCode;
import dev.lotnest.sequoia.core.ws.type.IStateOpCodeType;
import java.time.OffsetDateTime;
import java.util.Map;

public class GuildMapDataIStateOpCode extends IStateOpCode {
    public GuildMapDataIStateOpCode(Data data) {
        super(IStateOpCodeType.GUILD_MAP.getValue(), GSON.toJsonTree(data));
    }

    public record Data(
            @SerializedName("territory_data") Map<String, TerritoryData> territoryData, OffsetDateTime timestamp) {
        public record TerritoryData(String guild, String tag, byte treasury, Stored stored, Generation generation) {
            public record Stored(String guild, long emeralds, long ore, long wood, long crop, long fish) {}

            public record Generation(String guild, long emeralds, long ore, long wood, long crop, long fish) {}
        }
    }
}
