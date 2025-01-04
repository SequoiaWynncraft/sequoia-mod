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
    private static final int X1 = 13500, Y1 = 100, Z1 = -3600;
    private static final int X2 = 15000, Y2 = 300, Z2 = -3000;

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
        if (SequoiaMod.CONFIG.outerVoidItemFeature.enabled() && isWithinBox(x, y, z)) {
            float scale = SequoiaMod.CONFIG.outerVoidItemFeature.scale();
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
        return x >= X1 && x <= X2 && y >= Y1 && y <= Y2 && z >= Z1 && z <= Z2;
    }
}
