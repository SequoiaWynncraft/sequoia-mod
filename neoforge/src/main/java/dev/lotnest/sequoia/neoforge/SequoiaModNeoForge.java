/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.neoforge;

import com.wynntils.core.WynntilsMod;
import dev.lotnest.sequoia.SequoiaMod;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(SequoiaMod.MOD_ID)
public class SequoiaModNeoForge {
    public SequoiaModNeoForge() {
        SequoiaMod.init(
                SequoiaMod.ModLoader.FORGE,
                !FMLEnvironment.production,
                ModLoadingContext.get()
                        .getActiveContainer()
                        .getModInfo()
                        .getVersion()
                        .toString());

        WynntilsMod.registerEventListener(this);
    }
}
