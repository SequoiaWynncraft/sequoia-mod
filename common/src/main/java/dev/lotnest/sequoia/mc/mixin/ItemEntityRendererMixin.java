/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.mc.extension.EntityRenderStateExtension;
import com.wynntils.mc.extension.ItemStackRenderStateExtension;
import dev.lotnest.sequoia.SequoiaMod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
abstract class ItemEntityRendererMixin {
    @Unique
    private static final int OUTER_VOID_MIN_X = 13500;

    @Unique
    private static final int OUTER_VOID_MIN_Y = 100;

    @Unique
    private static final int OUTER_VOID_MIN_Z = -3600;

    @Unique
    private static final int OUTER_VOID_MAX_X = 15000;

    @Unique
    private static final int OUTER_VOID_MAX_Y = 300;

    @Unique
    private static final int OUTER_VOID_MAX_Z = -3000;

    @Inject(
            method =
                    "extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V",
            at = @At("RETURN"))
    private void onExtractRenderState(
            ItemEntity itemEntity, ItemEntityRenderState itemEntityRenderState, float f, CallbackInfo ci) {
        if (itemEntityRenderState.item instanceof ItemStackRenderStateExtension itemStackRenderStateExtension) {
            itemStackRenderStateExtension.setItemStack(itemEntity.getItem());
        }
    }

    @Inject(
            method =
                    "render(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"))
    private void onRender(
            ItemEntityRenderState itemEntityRenderState,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            CallbackInfo ci) {
        if (SequoiaMod.CONFIG.outerVoidTrackerFeature.enabled()) {
            Entity entity = ((EntityRenderStateExtension) itemEntityRenderState).getEntity();
            int x = (int) entity.getX();
            int y = (int) entity.getY();
            int z = (int) entity.getZ();

            if (isWithinBox(x, y, z)) {
                poseStack.pushPose();
                float scale = SequoiaMod.CONFIG.outerVoidTrackerFeature.scale();
                poseStack.scale(scale, scale, scale);
            }
        }
    }

    @Inject(
            method =
                    "render(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL"))
    private void afterRender(
            ItemEntityRenderState itemEntityRenderState,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            CallbackInfo ci) {
        if (SequoiaMod.CONFIG.outerVoidTrackerFeature.enabled()) {
            Entity entity = ((EntityRenderStateExtension) itemEntityRenderState).getEntity();
            int x = (int) entity.getX();
            int y = (int) entity.getY();
            int z = (int) entity.getZ();

            if (isWithinBox(x, y, z)) {
                poseStack.popPose();
            }
        }
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
