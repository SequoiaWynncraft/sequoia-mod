package dev.lotnest.sequoia.mixins;

import com.wynntils.core.components.Models;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.ws.SequoiaWebSocketClient;
import dev.lotnest.sequoia.wynn.WynnUtils;
import java.net.URI;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.java_websocket.enums.ReadyState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    protected ClientPacketListenerMixin(
            Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(
            method = "handlePlayerInfoUpdate(Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;)V",
            at = @At("RETURN"))
    private void handlePlayerInfoUpdatePost(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        if (!McUtils.mc().isSameThread()) {
            return;
        }

        if (!Models.WorldState.onWorld()) {
            return;
        }

        for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
            for (ClientboundPlayerInfoUpdatePacket.Action action : packet.actions()) {
                if (action == ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME) {
                    if (entry.displayName() == null) {
                        continue;
                    }

                    if (WynnUtils.isWynncraftWorld(entry.displayName().getString())) {
                        try {
                            if (SequoiaMod.getWebSocketClient() == null) {
                                SequoiaMod.setWebSocketClient(new SequoiaWebSocketClient(
                                        URI.create(
                                                SequoiaMod.isDevelopmentEnvironment()
                                                        ? SequoiaWebSocketClient.WS_DEV_URL
                                                        : SequoiaWebSocketClient.WS_PROD_URL),
                                        Map.of(
                                                "Authoworization",
                                                "Bearer meowmeowAG6v92hc23LK5rqrSD279",
                                                "X-UUID",
                                                McUtils.player().getStringUUID())));
                            }

                            if (SequoiaMod.getWebSocketClient().getReadyState() == ReadyState.NOT_YET_CONNECTED) {
                                SequoiaMod.getWebSocketClient().connect();
                            } else if (SequoiaMod.getWebSocketClient().getReadyState() == ReadyState.CLOSED) {
                                SequoiaMod.getWebSocketClient().reconnect();
                            }
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
