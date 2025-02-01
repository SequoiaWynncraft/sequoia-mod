/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.wynntils.core.components.Models;
import com.wynntils.models.territories.TerritoryInfo;
import com.wynntils.services.map.pois.TerritoryPoi;
import dev.lotnest.sequoia.core.components.Model;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerritoryModel extends Model {
    private static final Pattern TERRITORY_CAPTURED_PATTERN =
            Pattern.compile("^(?<guild>.+?) has taken control of (?<territory>.+?)!$");

    public TerritoryModel() {
        super(List.of());
    }

    public Pair<String, String> parseTerritoryCapturedMessage(String message) {
        Matcher territoryCapturedMatcher = TERRITORY_CAPTURED_PATTERN.matcher(message);
        if (territoryCapturedMatcher.matches()) {
            return Pair.of(territoryCapturedMatcher.group("guild"), territoryCapturedMatcher.group("territory"));
        }
        return null;
    }

    public TerritoryInfo getTerritoryInfo(String territoryName) {
        TerritoryPoi territoryPoi = Models.Territory.getTerritoryPoiFromAdvancement(territoryName);
        if (territoryPoi != null) {
            return territoryPoi.getTerritoryInfo();
        }
        return null;
    }
}
