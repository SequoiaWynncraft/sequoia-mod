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

public class GIC3HWSMessage extends WSMessage {
    public GIC3HWSMessage(Data data) {
        super(WSMessageType.G_IC3H.getValue(), GSON.toJsonTree(data));
    }

    public Data getGIC3HData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            @SerializedName("op_code") int opCode, int sequence, String method, byte[] payload, String[] target) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return opCode == data.opCode
                    && sequence == data.sequence
                    && Objects.equals(method, data.method)
                    && Objects.deepEquals(payload, data.payload)
                    && Objects.deepEquals(target, data.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(opCode, sequence, method, Arrays.hashCode(payload), Arrays.hashCode(target));
        }

        @Override
        public String toString() {
            return "Data{" + "opCode="
                    + opCode + ", sequence="
                    + sequence + ", method='"
                    + method + '\'' + ", payload="
                    + Arrays.toString(payload) + ", target="
                    + Arrays.toString(target) + '}';
        }
    }
}
