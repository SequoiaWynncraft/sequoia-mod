/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.war;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;

public class WarPartyFeature extends Feature {
    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.warPartyFeature.enabled();
    }
}
