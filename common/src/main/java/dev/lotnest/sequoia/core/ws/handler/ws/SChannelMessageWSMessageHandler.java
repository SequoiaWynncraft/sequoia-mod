/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.handler.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import com.wynntils.models.guild.type.GuildRank;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.text.Fonts;
import dev.lotnest.sequoia.core.ws.handler.WSMessageHandler;
import dev.lotnest.sequoia.core.ws.message.ws.discordchatbridge.SChannelMessageWSMessage;
import java.util.Locale;
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
            String[] displayNameSplit =
                    sChannelMessageWSMessageData.displayName().split(" ");
            String playerName = displayNameSplit.length > 1 ? displayNameSplit[1] : displayNameSplit[0];

            McUtils.sendMessageToClient(Fonts.BannerPill.parse("discord")
                    .withColor(0x7289DA)
                    .append(Component.literal(" "))
                    .append(Fonts.BannerPill.parse(getGuildRank(sChannelMessageWSMessageData.sequoiaRoles()))
                            .withStyle(ChatFormatting.AQUA))
                    .append(Fonts.Default.parse(" " + playerName + ": ").withStyle(ChatFormatting.DARK_AQUA))
                    .append(Fonts.Default.parse(sChannelMessageWSMessageData.message())
                            .withStyle(ChatFormatting.AQUA)));
        }
    }

    private String getGuildRank(String[] discordRoles) {
        for (GuildRank guildRank : GuildRank.values()) {
            for (String discordRole : discordRoles) {
                if (guildRank.getName().equalsIgnoreCase(discordRole)) {
                    return guildRank.getName().toLowerCase(Locale.ROOT);
                }
            }
        }
        return GuildRank.RECRUIT.getName().toLowerCase(Locale.ROOT);
    }
}
