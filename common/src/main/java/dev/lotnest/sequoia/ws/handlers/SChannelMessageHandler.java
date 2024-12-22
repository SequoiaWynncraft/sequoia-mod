package dev.lotnest.sequoia.ws.handlers;

import static dev.lotnest.sequoia.ws.SequoiaWebSocketClient.GSON;

import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.features.discordchatbridge.SChannelMessageWSMessage;
import dev.lotnest.sequoia.ws.WSMessageHandler;
import net.minecraft.network.chat.Component;

public class SChannelMessageHandler extends WSMessageHandler {
    public SChannelMessageHandler(String message) {
        super(GSON.fromJson(message, SChannelMessageWSMessage.class), message);
    }

    @Override
    public void handle() {
        if (SequoiaMod.CONFIG.discordChatBridgeFeature.sendDiscordMessagesToInGameChat()) {
            SChannelMessageWSMessage sChannelMessageWSMessage = (SChannelMessageWSMessage) wsMessage;
            SChannelMessageWSMessage.Data sChannelMessageWSMessageData =
                    sChannelMessageWSMessage.getChannelMessageData();

            McUtils.sendMessageToClient(Component.literal("[DISCORD] " + sChannelMessageWSMessageData.displayName()
                            + " ➤ " + sChannelMessageWSMessageData.message())
                    .withStyle(style -> style.withColor(0x5865F2)));
        }
    }
}
