/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.mixins;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.features.WebSocketFeature;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ClientCommonPacketListenerImplMixin {
    @Shadow
    @Final
    protected ServerData serverData;

    @Inject(
            method = "handleDisconnect(Lnet/minecraft/network/protocol/common/ClientboundDisconnectPacket;)V",
            at = @At("HEAD"))
    private void handleDisconnect(ClientboundDisconnectPacket packet, CallbackInfo ci) {
        WebSocketFeature webSocketFeature = SequoiaMod.getWebSocketFeature();
        if (webSocketFeature != null
                && webSocketFeature.isEnabled()
                && serverData != null
                && WynnUtils.isWynncraftServer(serverData.ip)) {
            webSocketFeature.closeIfNeeded();
        }
    }
}
