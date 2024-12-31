package dev.lotnest.sequoia.minecraft;

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
