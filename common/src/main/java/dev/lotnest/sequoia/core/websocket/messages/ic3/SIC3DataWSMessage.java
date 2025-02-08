/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket.messages.ic3;

import static dev.lotnest.sequoia.features.WebSocketFeature.GSON;

import com.google.gson.annotations.SerializedName;
import dev.lotnest.sequoia.core.websocket.WSMessage;
import dev.lotnest.sequoia.core.websocket.WSMessageType;
import java.util.Arrays;
import java.util.Objects;

public class SIC3DataWSMessage extends WSMessage {
    public SIC3DataWSMessage(Data data) {
        super(WSMessageType.S_IC3_DATA.getValue(), GSON.toJsonTree(data));
    }

    public Data getSIC3Data() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            @SerializedName("op_code") int opCode, int sequence, String method, byte[] payload, String origin) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return opCode == data.opCode
                    && sequence == data.sequence
                    && Objects.equals(method, data.method)
                    && Objects.deepEquals(payload, data.payload)
                    && Objects.equals(origin, data.origin);
        }

        @Override
        public int hashCode() {
            return Objects.hash(opCode, sequence, method, Arrays.hashCode(payload), origin);
        }

        @Override
        public String toString() {
            return "Data{" + "opCode="
                    + opCode + ", sequence="
                    + sequence + ", method='"
                    + method + '\'' + ", payload="
                    + Arrays.toString(payload) + ", origin='"
                    + origin + '\'' + '}';
        }
    }
}
