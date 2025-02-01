/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.components;

import dev.lotnest.sequoia.models.GambitModel;
import dev.lotnest.sequoia.models.RaidModel;
import dev.lotnest.sequoia.models.TerritoryModel;

public final class Models {
    public static final TerritoryModel Territory = new TerritoryModel();
    public static final RaidModel Raid = new RaidModel();
    public static final GambitModel Gambit = new GambitModel();

    private Models() {}
}
