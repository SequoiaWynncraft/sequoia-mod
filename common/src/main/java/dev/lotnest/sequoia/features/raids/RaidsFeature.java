/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.raids;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.PlayerRenderEvent;
import com.wynntils.mc.extension.EntityRenderStateExtension;
import com.wynntils.utils.colors.CommonColors;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Feature;
import dev.lotnest.sequoia.utils.mc.PlayerUtils;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class RaidsFeature extends Feature {
    private static final MultiBufferSource.BufferSource BUFFER_SOURCE =
            MultiBufferSource.immediate(new ByteBufferBuilder(256));

    private static final int CIRCLE_SEGMENTS = 128;

    private static final float CIRCLE_HEIGHT = 0.2F;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRender(PlayerRenderEvent event) {
        if (!isEnabled()) {
            return;
        }

        Entity entity = ((EntityRenderStateExtension) event.getPlayerRenderState()).getEntity();
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }

        if (!Models.WorldState.onWorld() && !Models.WorldState.onHousing()) {
            return;
        }

        if (!PlayerUtils.isSelf(player)) {
            return;
        }

        if (SequoiaMod.CONFIG.raidsFeature.farsightedOverlay()) {
            WynnUtils.renderCircle(
                    BUFFER_SOURCE,
                    CIRCLE_SEGMENTS,
                    CIRCLE_HEIGHT,
                    event.getPoseStack(),
                    player.position(),
                    3.0F,
                    CommonColors.LIGHT_BLUE.withAlpha((95)).asInt());
        }
        if (SequoiaMod.CONFIG.raidsFeature.myopicOverlay()) {
            WynnUtils.renderCircle(
                    BUFFER_SOURCE,
                    CIRCLE_SEGMENTS,
                    CIRCLE_HEIGHT,
                    event.getPoseStack(),
                    player.position(),
                    12.0F,
                    CommonColors.LIGHT_GREEN.withAlpha((95)).asInt());
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }
}
