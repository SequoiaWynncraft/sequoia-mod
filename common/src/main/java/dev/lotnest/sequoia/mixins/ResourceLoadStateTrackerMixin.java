package dev.lotnest.sequoia.mixins;

import dev.lotnest.sequoia.SequoiaMod;
import net.minecraft.client.ResourceLoadStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceLoadStateTracker.class)
public abstract class ResourceLoadStateTrackerMixin {
    @Inject(method = "finishReload()V", at = @At("RETURN"))
    private void onResourceManagerReloadPost(CallbackInfo info) {
        SequoiaMod.onResourcesFinishedLoading();
    }
}
