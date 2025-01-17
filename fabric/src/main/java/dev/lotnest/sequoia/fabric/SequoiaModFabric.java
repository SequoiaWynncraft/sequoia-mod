/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.fabric;

import dev.lotnest.sequoia.SequoiaMod;
import java.util.Optional;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class SequoiaModFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Optional<ModContainer> sequoiaModContainer = FabricLoader.getInstance().getModContainer("sequoia");
        if (sequoiaModContainer.isEmpty()) {
            SequoiaMod.error("Sequoia mod not found");
            return;
        }

        SequoiaMod.init(
                SequoiaMod.ModLoader.FABRIC,
                FabricLoader.getInstance().isDevelopmentEnvironment(),
                sequoiaModContainer.get().getMetadata().getVersion().getFriendlyString());
    }
}
