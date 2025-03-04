/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.message;

import blue.endless.jankson.annotation.SerializedName;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class IStateOpCode {
    @SerializedName("op_code")
    private final int opCode;

    private final JsonElement data;

    public IStateOpCode(int opCode, JsonElement data) {
        this.opCode = opCode;
        this.data = data;
    }

    public int getOpCode() {
        return opCode;
    }

    public JsonElement getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        IStateOpCode wsMessage = (IStateOpCode) o;

        return new EqualsBuilder()
                .append(opCode, wsMessage.opCode)
                .append(data, wsMessage.data)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(opCode).append(data).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", opCode)
                .append("data", data)
                .toString();
    }
}
