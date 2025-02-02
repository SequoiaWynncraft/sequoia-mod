/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.managers;

import com.google.common.collect.Maps;
import dev.lotnest.sequoia.core.components.Manager;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.CrashReportCategory;

public final class CrashReportManager extends Manager {
    private static final Map<String, Supplier<String>> CRASH_HANDLERS = Maps.newHashMap();

    public CrashReportManager() {
        super(List.of());
    }

    public void registerCrashContext(String name, Supplier<String> handler) {
        CRASH_HANDLERS.put(name, handler);
    }

    //  Note: this is called directly from a mixin!
    public static CrashReportCategory generateDetails() {
        CrashReportCategory sequoiaCategory = new CrashReportCategory("Sequoia");

        for (Map.Entry<String, Supplier<String>> entry : CRASH_HANDLERS.entrySet()) {
            String report = entry.getValue().get();
            if (report != null) {
                sequoiaCategory.setDetail(entry.getKey(), report);
            }
        }

        return sequoiaCategory;
    }
}
