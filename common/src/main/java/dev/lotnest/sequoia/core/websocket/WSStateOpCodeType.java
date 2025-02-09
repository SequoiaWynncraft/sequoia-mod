/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket;

public enum WSStateOpCodeType {
    INVALID,
    LOCATION_SERVICE,
    LOOT_POOL,
    GUILD_MAP,
    GUILD_WAR_RESULTS;

    public static WSStateOpCodeType fromValue(int value) {
        if (value < 0 || value >= values().length) {
            return INVALID;
        }
        return values()[value];
    }

    public int getValue() {
        return ordinal();
    }
}
