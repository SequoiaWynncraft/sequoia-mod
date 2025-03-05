/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.istateopcodes;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.core.ws.message.IStateOpCode;
import dev.lotnest.sequoia.core.ws.type.IStateOpCodeType;
import java.util.Arrays;
import java.util.Objects;

public class GuildWarResultsDataIStateOpCode extends IStateOpCode {
    public GuildWarResultsDataIStateOpCode(Data data) {
        super(IStateOpCodeType.GUILD_WAR_RESULTS.getValue(), GSON.toJsonTree(data));
    }

    public record Data(
            String territory,
            @SerializedName("guild_war_members") String[] guildWarUsernames,
            @SerializedName("war_time") double warTimeSeconds,
            int sr,
            Results results) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return Double.compare(data.warTimeSeconds, warTimeSeconds) == 0
                    && sr == data.sr
                    && territory.equals(data.territory)
                    && Arrays.equals(guildWarUsernames, data.guildWarUsernames)
                    && results.equals(data.results);
        }

        @Override
        public int hashCode() {
            return Objects.hash(territory, Arrays.hashCode(guildWarUsernames), warTimeSeconds, sr, results);
        }

        @Override
        public String toString() {
            return "Data{" + "territory='"
                    + territory + '\'' + ", guildWarUsernames="
                    + Arrays.toString(guildWarUsernames) + ", warTimeSeconds="
                    + warTimeSeconds + ", sr="
                    + sr + ", results="
                    + results + '}';
        }

        public record Results(int damage, double attack, int health, @SerializedName("defence") double defenceRatio) {}
    }
}
