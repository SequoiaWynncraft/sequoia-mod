/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message.istateopcodes;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import blue.endless.jankson.annotation.SerializedName;
import com.google.gson.JsonElement;
import dev.lotnest.sequoia.core.ws.message.IStateOpCode;
import dev.lotnest.sequoia.core.ws.type.IStateOpCodeType;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class LootPoolDataIStateOpCode extends IStateOpCode {
    public LootPoolDataIStateOpCode(Data data) {
        super(IStateOpCodeType.LOOT_POOL.getValue(), GSON.toJsonTree(data));
    }

    public record Data(
            LootPoolKind kind, String name, @SerializedName("lootpool") JsonElement entries, String[] gambit) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return kind == data.kind
                    && StringUtils.equals(name, data.name)
                    && Objects.equals(entries, data.entries)
                    && Arrays.equals(gambit, data.gambit);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kind, name, entries, Arrays.hashCode(gambit));
        }

        @Override
        public String toString() {
            return "Data{" + "kind="
                    + kind + ", name='"
                    + name + '\'' + ", entries="
                    + entries + ", gambit="
                    + Arrays.toString(gambit) + '}';
        }
    }

    public enum LootPoolKind {
        LOOTRUN,
        RAID;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
