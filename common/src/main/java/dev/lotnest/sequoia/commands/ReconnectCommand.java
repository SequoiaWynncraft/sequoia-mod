/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Managers;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.command.Command;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
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
        if (SequoiaMod.getWebSocketFeature() == null
                || !SequoiaMod.getWebSocketFeature().isEnabled()) {
            context.getSource()
                    .sendFailure(
                            SequoiaMod.prefix(Component.translatable("sequoia.feature.webSocket.featureDisabled")));
            return 1;
        }

        if (Boolean.FALSE.equals(WynnUtils.isSequoiaGuildMember().join())) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.notASequoiaGuildMember")));
            return 1;
        }

        if (SequoiaMod.getWebSocketFeature().getClient() == null) {
            SequoiaMod.getWebSocketFeature().initClient();
        }

        context.getSource()
                .sendSuccess(
                        () -> SequoiaMod.prefix(Component.translatable("sequoia.command.reconnect.reconnecting")),
                        false);
        SequoiaMod.getWebSocketFeature().closeIfNeeded();
        SequoiaMod.getWebSocketFeature().connectIfNeeded();
        Managers.TickScheduler.scheduleLater(
                () -> {
                    if (!SequoiaMod.getWebSocketFeature().getClient().isOpen()) {
                        context.getSource()
                                .sendFailure(SequoiaMod.prefix(
                                        Component.translatable("sequoia.command.reconnect.failedToReconnect")));
                    }
                },
                20 * 10);

        return 1;
    }
}
