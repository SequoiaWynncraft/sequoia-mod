package dev.lotnest.sequoia.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lotnest.sequoia.feature.features.CustomDeathSoundFeature;
import dev.lotnest.sequoia.manager.Managers;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(
            method = "play",
            at =
                    @At(
                            value = "FIELD",
                            target =
                                    "Lnet/minecraft/client/sounds/SoundEngine;instanceBySource:Lcom/google/common/collect/Multimap;"))
    private void play(SoundInstance sound, CallbackInfo callbackInfo, @Local ResourceLocation identifier) {
        if (!StringUtils.equalsAny(
                identifier.toString(), "minecraft:wynn.world.soul", "minecraft:item.trident.thunder")) {
            return;
        }

        if (!Managers.Feature.getFeatureInstance(CustomDeathSoundFeature.class).isEnabled()) {
            return;
        }

        try {
            callbackInfo.cancel();
        } catch (CancellationException ignored) {
        }
    }
}
