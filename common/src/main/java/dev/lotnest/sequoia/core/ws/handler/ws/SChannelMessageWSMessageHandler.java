/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.handler.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.ws.handler.WSMessageHandler;
import dev.lotnest.sequoia.core.ws.message.ws.discordchatbridge.SChannelMessageWSMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SChannelMessageWSMessageHandler extends WSMessageHandler {
    public SChannelMessageWSMessageHandler(String message) {
        super(GSON.fromJson(message, SChannelMessageWSMessage.class), message);
    }

    @Override
    public void handle() {
        if (SequoiaMod.CONFIG.discordChatBridgeFeature.enabled()
                && SequoiaMod.CONFIG.discordChatBridgeFeature.sendDiscordMessagesToInGameChat()) {
            SChannelMessageWSMessage sChannelMessageWSMessage = (SChannelMessageWSMessage) wsMessage;
            SChannelMessageWSMessage.Data sChannelMessageWSMessageData =
                    sChannelMessageWSMessage.getSChannelMessageData();

            McUtils.sendMessageToClient(Component.literal("[DISCORD] ")
                    .withStyle(ChatFormatting.AQUA)
                    .append(Component.literal(sChannelMessageWSMessageData.displayName() + ": ")
                            .withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.literal(sChannelMessageWSMessageData.message())
                            .withStyle(ChatFormatting.AQUA)));
        }
    }
}
