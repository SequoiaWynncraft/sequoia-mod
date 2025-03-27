/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.lotnest.sequoia.SequoiaMod;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinHeldItemRenderer {
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void onRenderHandsPos(
            LivingEntity entity,
            ItemStack item,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack ps,
            MultiBufferSource multiBufferSource,
            int light,
            CallbackInfo ci) {
        ps.pushPose();

        ps.mulPose(Axis.XP.rotationDegrees(SequoiaMod.CONFIG.itemSizeFeature.RotationX()));
        ps.mulPose(Axis.YP.rotationDegrees(SequoiaMod.CONFIG.itemSizeFeature.RotationY()));
        ps.mulPose(Axis.ZP.rotationDegrees(SequoiaMod.CONFIG.itemSizeFeature.RotationZ()));
        ps.translate(
                SequoiaMod.CONFIG.itemSizeFeature.PositionX(),
                SequoiaMod.CONFIG.itemSizeFeature.PositionY(),
                SequoiaMod.CONFIG.itemSizeFeature.PositionZ());
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void scaleItems(
            LivingEntity entity,
            ItemStack item,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack ps,
            MultiBufferSource multiBufferSource,
            int light,
            CallbackInfo ci) {
        if (SequoiaMod.CONFIG.itemSizeFeature.enabled()) {
            float scale = SequoiaMod.CONFIG.itemSizeFeature.itemSize();
            ps.scale(scale, scale, scale);
        }
    }

    @Inject(method = "renderItem", at = @At("TAIL"))
    private void popMatrix(
            LivingEntity entity,
            ItemStack item,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack ps,
            MultiBufferSource multiBufferSource,
            int light,
            CallbackInfo ci) {
        ps.popPose();
    }
}
