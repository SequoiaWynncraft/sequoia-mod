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
import dev.lotnest.sequoia.core.websocket.messages.session.GAuthWSMessage;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.regex.Pattern;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class AuthCommand extends Command {
    private static final Pattern CODE_PATTERN = Pattern.compile("[a-z0-9]{64}");

    private boolean sentGAuthWSMessage = false;

    @Override
    public String getCommandName() {
        return "auth";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.argument("code", StringArgumentType.word()).executes(this::authenticate))
                .executes(this::syntaxError);
    }

    private int authenticate(CommandContext<CommandSourceStack> context) {
        String code = context.getArgument("code", String.class);
        if (CODE_PATTERN.matcher(code).matches()) {
            if (SequoiaMod.getWebSocketFeature() == null
                    || !SequoiaMod.getWebSocketFeature().isEnabled()) {
                context.getSource()
                        .sendFailure(
                                SequoiaMod.prefix(Component.translatable("sequoia.feature.webSocket.featureDisabled")));
                return 1;
            }

            if (Boolean.FALSE.equals(WynnUtils.isSequoiaGuildMember().join())) {
                context.getSource()
                        .sendFailure(
                                SequoiaMod.prefix(Component.translatable("sequoia.command.notASequoiaGuildMember")));
                return 1;
            }

            if (SequoiaMod.getWebSocketFeature().isAuthenticated()) {
                context.getSource()
                        .sendFailure(
                                SequoiaMod.prefix(Component.translatable("sequoia.command.auth.alreadyAuthenticated")));
                return 1;
            }

            if (sentGAuthWSMessage) {
                context.getSource()
                        .sendFailure(SequoiaMod.prefix(
                                Component.translatable("sequoia.command.auth.pleaseWaitBeforeRetrying")));
                return 1;
            }

            if (!SequoiaMod.getWebSocketFeature().getClient().isOpen()) {
                SequoiaMod.getWebSocketFeature().connectIfNeeded();
            }

            GAuthWSMessage gAuthWSMessage = new GAuthWSMessage(code);
            SequoiaMod.getWebSocketFeature().sendAsJson(gAuthWSMessage);
            sentGAuthWSMessage = true;
            context.getSource()
                    .sendSuccess(
                            () -> SequoiaMod.prefix(Component.translatable("sequoia.command.auth.authenticating")),
                            false);

            Managers.TickScheduler.scheduleLater(() -> sentGAuthWSMessage = false, 20 * 10);
        } else {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.auth.invalidCode")));
        }
        return 1;
    }
}
