/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc;

import dev.lotnest.sequoia.SequoiaMod;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;

public final class SharedPanoramaRenderer {
    private static final CubeMap PANORAMA_CUBE_MAP = new CubeMap(
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "textures/gui/title/panorama/panorama"));
    public static final PanoramaRenderer INSTANCE = new PanoramaRenderer(PANORAMA_CUBE_MAP);

    private SharedPanoramaRenderer() {}
}
