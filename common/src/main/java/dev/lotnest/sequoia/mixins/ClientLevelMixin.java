package dev.lotnest.sequoia.mixins;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.features.WebSocketFeature;
import dev.lotnest.sequoia.wynn.WynnUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @Shadow
    @Final
    private ClientPacketListener connection;

    @Inject(method = "disconnect()V", at = @At("HEAD"))
    private void disconnect(CallbackInfo ci) {
        WebSocketFeature webSocketFeature = SequoiaMod.getWebSocketFeature();
        if (webSocketFeature != null
                && webSocketFeature.isEnabled()
                && connection.getServerData() != null
                && WynnUtils.isWynncraftServer(connection.getServerData().ip)) {
            webSocketFeature.closeIfNeeded();
        }
    }
}
