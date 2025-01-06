package dev.lotnest.sequoia.mixins;

import com.wynntils.core.components.Models;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.features.WebSocketFeature;
import dev.lotnest.sequoia.upfixers.AccessTokenManagerUpfixer;
import dev.lotnest.sequoia.wynn.WynnUtils;
import dev.lotnest.sequoia.wynn.api.guild.GuildService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    @Unique
    private boolean isFirstJoin = true;

    protected ClientPacketListenerMixin(
            Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(
            method = "handlePlayerInfoUpdate(Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;)V",
            at = @At("RETURN"))
    private void handlePlayerInfoUpdatePost(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        if (!Models.WorldState.onWorld() && !Models.WorldState.onHousing()) {
            return;
        }

        if (!isFirstJoin) {
            return;
        }

        WebSocketFeature webSocketFeature = SequoiaMod.getWebSocketFeature();
        if (webSocketFeature == null || !webSocketFeature.isEnabled()) {
            return;
        }

        if (!GuildService.isSequoiaGuildMember()) {
            return;
        }

        for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
            for (ClientboundPlayerInfoUpdatePacket.Action action : packet.actions()) {
                if (action == ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME) {
                    if (entry.displayName() == null) {
                        continue;
                    }

                    if (WynnUtils.isWynncraftWorld(entry.displayName().getString())) {
                        isFirstJoin = true;

                        AccessTokenManagerUpfixer.fixLegacyFiles();

                        try {
                            webSocketFeature.initClient();
                            webSocketFeature.connectIfNeeded();
                        } catch (Exception exception) {
                            SequoiaMod.error("Failed to connect to WebSocket server: " + exception.getMessage());
                        }
                        break;
                    }
                }
            }
        }
    }
}
