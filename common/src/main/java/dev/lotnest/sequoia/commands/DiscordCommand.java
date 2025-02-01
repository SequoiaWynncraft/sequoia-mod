/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

public class DiscordCommand extends Command {
    @Override
    public String getCommandName() {
        return "discord";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::sendDiscordInfoMessage);
    }

    private int sendDiscordInfoMessage(CommandContext<CommandSourceStack> context) {
        McUtils.sendMessageToClient(SequoiaMod.prefix(Component.translatable("sequoia.command.discord.message"))
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.com/invite/seq"))));
        return 1;
    }
}
