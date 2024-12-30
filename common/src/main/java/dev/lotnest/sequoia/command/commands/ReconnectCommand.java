package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Managers;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.wynn.api.guild.GuildService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class ReconnectCommand extends Command {
    @Override
    public String getCommandName() {
        return "reconnect";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::reconnectToWebSocket);
    }

    private int reconnectToWebSocket(CommandContext<CommandSourceStack> context) {
        if (!GuildService.isSequoiaGuildMember()) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.notASequoiaGuildMember")));
            return 1;
        }

        context.getSource()
                .sendSuccess(
                        () -> SequoiaMod.prefix(Component.translatable("sequoia.command.reconnect.reconnecting")),
                        false);
        SequoiaMod.getWebSocketClient().closeIfNeeded();
        SequoiaMod.getWebSocketClient().connectIfNeeded();
        Managers.TickScheduler.scheduleLater(
                () -> {
                    if (!SequoiaMod.getWebSocketClient().isOpen()) {
                        context.getSource()
                                .sendFailure(SequoiaMod.prefix(
                                        Component.translatable("sequoia.command.reconnect.failedToReconnect")));
                    }
                },
                20 * 10);

        return 1;
    }
}
