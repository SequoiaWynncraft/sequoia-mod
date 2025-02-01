/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.territory;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Models;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.core.consumers.features.properties.RegisterKeyBind;
import dev.lotnest.sequoia.core.keybinds.KeyBind;
import org.lwjgl.glfw.GLFW;

public class TerritoryMenuHotkeyFeature extends Feature {
    @RegisterKeyBind
    private final KeyBind territoryMenuKeybind =
            new KeyBind("Open Territory Menu", GLFW.GLFW_KEY_N, true, this::onOpenTerritoryMenuKeyPress);

    private void onOpenTerritoryMenuKeyPress() {
        if (!isEnabled()) {
            return;
        }
        Models.Territory.openTerritoryMenu();
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.territoryFeature.enabled() && SequoiaMod.CONFIG.territoryFeature.territoryMenuHotkey();
    }
}
