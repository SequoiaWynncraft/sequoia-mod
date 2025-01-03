package dev.lotnest.sequoia.mixins;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.minecraft.SharedPanoramaRenderer;
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
        if (!SequoiaMod.CONFIG.titleScreenEnhancementsFeature.enabled()) {
            return;
        }

        if (!SequoiaMod.CONFIG.titleScreenEnhancementsFeature.showSequoiaPanorama()) {
            return;
        }

        SharedPanoramaRenderer.INSTANCE.render(
                guiGraphics, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 1.0F, partialTick);
        ci.cancel();
    }
}
