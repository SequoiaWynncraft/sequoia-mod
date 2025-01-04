package dev.lotnest.sequoia.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lotnest.sequoia.SequoiaMod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
abstract class ItemEntityRendererMixin {
    private static final int OUTER_VOID_MIN_X = 13500, OUTER_VOID_MIN_Y = 100, OUTER_VOID_MIN_Z = -3600;
    private static final int OUTER_VOID_MAX_X = 15000, OUTER_VOID_MAX_Y = 300, OUTER_VOID_MAX_Z = -3000;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(
            ItemEntity entity,
            float yaw,
            float partialTicks,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            CallbackInfo info) {
        matrices.pushPose();
        int x = (int) entity.getX();
        int y = (int) entity.getY();
        int z = (int) entity.getZ();
        if (SequoiaMod.CONFIG.outerVoidTrackerFeature.enabled() && isWithinBox(x, y, z)) {
            float scale = SequoiaMod.CONFIG.outerVoidTrackerFeature.scale();
            matrices.scale(scale, scale, scale);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void afterRender(
            ItemEntity entity,
            float yaw,
            float partialTicks,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            CallbackInfo info) {
        matrices.popPose();
    }

    @Unique
    private boolean isWithinBox(int x, int y, int z) {
        return x >= OUTER_VOID_MIN_X
                && x <= OUTER_VOID_MAX_X
                && y >= OUTER_VOID_MIN_Y
                && y <= OUTER_VOID_MAX_Y
                && z >= OUTER_VOID_MIN_Z
                && z <= OUTER_VOID_MAX_Z;
    }
}
