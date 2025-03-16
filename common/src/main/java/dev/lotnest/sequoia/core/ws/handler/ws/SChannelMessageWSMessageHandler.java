/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.handler.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.text.Fonts;
import dev.lotnest.sequoia.core.ws.handler.WSMessageHandler;
import dev.lotnest.sequoia.core.ws.message.ws.discordchatbridge.SChannelMessageWSMessage;
import net.minecraft.ChatFormatting;

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

            McUtils.sendMessageToClient(Fonts.BannerPill.parse("discord")
                    .withStyle(ChatFormatting.AQUA)
                    .append(Fonts.Default.parse(" " + sChannelMessageWSMessageData.displayName() + ": ")
                            .withStyle(ChatFormatting.DARK_AQUA))
                    .append(Fonts.Default.parse(sChannelMessageWSMessageData.message())
                            .withStyle(ChatFormatting.AQUA)));
        }
    }
}
