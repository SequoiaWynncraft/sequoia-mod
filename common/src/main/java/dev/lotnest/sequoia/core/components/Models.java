/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.components;

import dev.lotnest.sequoia.models.GambitModel;
import dev.lotnest.sequoia.models.TerritoryModel;
import dev.lotnest.sequoia.models.War.WarModel;
import dev.lotnest.sequoia.models.War.WarPartyModel;
import dev.lotnest.sequoia.models.raid.RaidModel;

public final class Models {
    public static final TerritoryModel Territory = new TerritoryModel();
    public static final RaidModel Raid = new RaidModel();
    public static final GambitModel Gambit = new GambitModel();

    public static final WarModel War = new WarModel();
    public static final WarPartyModel WarParty = new WarPartyModel();

    private Models() {}
}
