package dev.lotnest.sequoia.manager;

import dev.lotnest.sequoia.manager.managers.ClientCommandManager;
import dev.lotnest.sequoia.manager.managers.CrashReportManager;
import dev.lotnest.sequoia.manager.managers.FeatureManager;

public final class Managers {
    public static final CrashReportManager CrashReport = new CrashReportManager();
    public static final FeatureManager Feature = new FeatureManager();
    public static final ClientCommandManager Command = new ClientCommandManager();
}
