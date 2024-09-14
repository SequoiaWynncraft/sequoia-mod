package dev.lotnest.sequoia.events;

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
