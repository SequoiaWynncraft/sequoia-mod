/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.mixin;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.mc.SharedPanoramaRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true)
    private void renderPanorama(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (!SequoiaMod.CONFIG.renderSequoiaPanorama()) {
            return;
        }

        SharedPanoramaRenderer.INSTANCE.render(
                guiGraphics, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 1.0F, partialTick);
        ci.cancel();
    }
}
