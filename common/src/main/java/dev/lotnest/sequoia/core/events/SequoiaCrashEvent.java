/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.events;

import com.wynntils.core.mod.type.CrashType;
import net.neoforged.bus.api.Event;

public class SequoiaCrashEvent extends Event {
    private final String name;
    private final CrashType type;
    private final Throwable throwable;

    public SequoiaCrashEvent(String name, CrashType type, Throwable throwable) {
        this.name = name;
        this.type = type;
        this.throwable = throwable;
    }

    public String getName() {
        return name;
    }

    public CrashType getType() {
        return type;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
