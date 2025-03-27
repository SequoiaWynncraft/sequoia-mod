/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.type;

public enum IStateOpCodeType {
    INVALID,
    LOCATION_SERVICE,
    LOOT_POOL,
    GUILD_MAP,
    GUILD_WAR_RESULTS;

    public static IStateOpCodeType fromValue(int value) {
        if (value < 0 || value >= values().length) {
            return INVALID;
        }
        return values()[value];
    }

    public int getValue() {
        return ordinal();
    }
}
