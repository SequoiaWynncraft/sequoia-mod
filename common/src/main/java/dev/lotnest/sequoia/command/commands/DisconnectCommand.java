/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Managers;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.feature.features.WebSocketFeature;
import dev.lotnest.sequoia.wynn.api.guild.GuildService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class DisconnectCommand extends Command {
    @Override
    public String getCommandName() {
        return "disconnect";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::disconnectFromWebSocket);
    }

    private int disconnectFromWebSocket(CommandContext<CommandSourceStack> context) {
        if (!dev.lotnest.sequoia.manager.Managers.Feature.getFeatureInstance(WebSocketFeature.class)
                .isEnabled()) {
            context.getSource()
                    .sendFailure(
                            SequoiaMod.prefix(Component.translatable("sequoia.feature.webSocket.featureDisabled")));
            return 1;
        }

        if (!GuildService.isSequoiaGuildMember()) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.notASequoiaGuildMember")));
            return 1;
        }

        if (SequoiaMod.getWebSocketFeature().getClient() == null
                || !SequoiaMod.getWebSocketFeature().getClient().isOpen()) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.disconnect.notConnected")));
            return 1;
        }

        context.getSource()
                .sendSuccess(
                        () -> SequoiaMod.prefix(Component.translatable("sequoia.command.disconnect.disconnecting")),
                        false);
        SequoiaMod.getWebSocketFeature().closeIfNeeded();
        Managers.TickScheduler.scheduleLater(
                () -> {
                    if (SequoiaMod.getWebSocketFeature().getClient().isClosed()) {
                        context.getSource()
                                .sendSuccess(
                                        () -> SequoiaMod.prefix(
                                                Component.translatable("sequoia.command.disconnect.disconnected")),
                                        false);
                        return;
                    }

                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(
                                    Component.translatable("sequoia.command.disconnect.failedToDisconnect")));
                },
                20 * 10);

        return 1;
    }
}
