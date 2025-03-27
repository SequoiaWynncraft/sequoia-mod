/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;

public class ItemSizeFeature extends Feature {
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.itemSizeFeature.enabled();
    }

    public float getSize() {
        return SequoiaMod.CONFIG.itemSizeFeature.itemSize();
    }
}
