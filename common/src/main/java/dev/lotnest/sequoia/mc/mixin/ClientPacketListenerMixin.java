/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.mixin;

import com.wynntils.core.events.MixinHelper;
import com.wynntils.core.text.StyledText;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.mc.event.ScoreboardEvent;
import dev.lotnest.sequoia.mc.event.ScoreboardSetDisplayObjectiveEvent;
import dev.lotnest.sequoia.mc.event.ScoreboardSetObjectiveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    protected ClientPacketListenerMixin(
            Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Unique
    private static boolean isRenderThread() {
        return McUtils.mc().isSameThread();
    }

    @Inject(
            method = "handleAddObjective(Lnet/minecraft/network/protocol/game/ClientboundSetObjectivePacket;)V",
            at = @At("RETURN"))
    private void handleAddObjective(ClientboundSetObjectivePacket packet, CallbackInfo ci) {
        if (!isRenderThread()) {
            return;
        }

        ScoreboardSetObjectiveEvent event = new ScoreboardSetObjectiveEvent(
                packet.getObjectiveName(), packet.getDisplayName(), packet.getRenderType(), packet.getMethod());
        MixinHelper.post(event);
    }

    @Inject(
            method = "handleSetScore(Lnet/minecraft/network/protocol/game/ClientboundSetScorePacket;)V",
            at = @At("RETURN"))
    private void handleSetScore(ClientboundSetScorePacket packet, CallbackInfo ci) {
        if (!isRenderThread()) {
            return;
        }

        ScoreboardEvent event =
                new ScoreboardEvent.Set(StyledText.fromString(packet.owner()), packet.objectiveName(), packet.score());
        MixinHelper.post(event);
    }

    @Inject(
            method = "handleResetScore(Lnet/minecraft/network/protocol/game/ClientboundResetScorePacket;)V",
            at = @At("RETURN"))
    private void handleResetScore(ClientboundResetScorePacket packet, CallbackInfo ci) {
        if (!isRenderThread()) {
            return;
        }

        ScoreboardEvent event =
                new ScoreboardEvent.Reset(StyledText.fromString(packet.owner()), packet.objectiveName());
        MixinHelper.post(event);
    }

    @Inject(
            method =
                    "handleSetDisplayObjective(Lnet/minecraft/network/protocol/game/ClientboundSetDisplayObjectivePacket;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket packet, CallbackInfo ci) {
        if (!isRenderThread()) {
            return;
        }

        ScoreboardSetDisplayObjectiveEvent event =
                new ScoreboardSetDisplayObjectiveEvent(packet.getSlot(), packet.getObjectiveName());
        MixinHelper.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
