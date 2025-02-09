/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Managers;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.command.Command;
import dev.lotnest.sequoia.core.websocket.messages.ic3.GIC3HWSMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class MessageCommand extends Command {
    private boolean sentGAuthWSMessage = false;

    @Override
    public String getCommandName() {
        return "Message";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.argument("message", StringArgumentType.word()).executes(this::sendMessage))
                .executes(this::syntaxError);
    }

    private int sendMessage(CommandContext<CommandSourceStack> context) {
        String message = context.getArgument("message", String.class);
        if (!SequoiaMod.getWebSocketFeature().getClient().isOpen()) {
            SequoiaMod.getWebSocketFeature().connectIfNeeded();
        }
        String[] target = new String[] {"*"};
        GIC3HWSMessage.Data data = new GIC3HWSMessage.Data(0, 0, "message", message.getBytes(), target);
        GIC3HWSMessage gic3HWSMessage = new GIC3HWSMessage(data);
        SequoiaMod.getWebSocketFeature().sendAsJson(gic3HWSMessage);
        sentGAuthWSMessage = true;
        context.getSource().sendSuccess(() -> SequoiaMod.prefix(Component.literal("§eSent message.")), false);

        Managers.TickScheduler.scheduleLater(() -> sentGAuthWSMessage = false, 20 * 10);
        return 1;
    }
}
