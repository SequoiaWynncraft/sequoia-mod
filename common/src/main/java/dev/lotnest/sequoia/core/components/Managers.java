/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.components;

import dev.lotnest.sequoia.core.consumers.command.ClientCommandManager;
import dev.lotnest.sequoia.core.consumers.features.FeatureManager;
import dev.lotnest.sequoia.managers.CrashReportManager;

public final class Managers {
    public static final CrashReportManager CrashReport = new CrashReportManager();
    public static final FeatureManager Feature = new FeatureManager();
    public static final ClientCommandManager Command = new ClientCommandManager();
}
