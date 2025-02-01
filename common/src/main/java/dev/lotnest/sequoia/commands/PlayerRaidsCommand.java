/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Services;
import dev.lotnest.sequoia.core.consumers.command.Command;
import dev.lotnest.sequoia.mc.MinecraftUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class PlayerRaidsCommand extends Command {
    @Override
    public String getCommandName() {
        return "playerRaids";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(
                        Commands.argument("username", StringArgumentType.word()).executes(this::lookupPlayerRaids))
                .executes(this::syntaxError);
    }

    private int lookupPlayerRaids(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        if (StringUtils.isBlank(username) || !MinecraftUtils.isValidUsername(username)) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.invalidUsername")));
        } else {
            Services.Player.getPlayer(username).whenComplete((playerResponse, throwable) -> {
                if (throwable != null) {
                    SequoiaMod.error("Error looking up player: " + username, throwable);
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(Component.translatable(
                                    "sequoia.command.playerRaids.errorLookingUpPlayer", username)));
                } else {
                    if (playerResponse == null
                            || playerResponse.getGlobalData() == null
                            || playerResponse.getGlobalData().getRaids() == null) {
                        context.getSource()
                                .sendFailure(SequoiaMod.prefix(Component.translatable(
                                        "sequoia.command.playerRaids.playerNotFound", username)));
                    } else {
                        if (playerResponse.getGlobalData().getRaids().getTotal() == 0) {
                            context.getSource()
                                    .sendFailure(SequoiaMod.prefix(Component.translatable(
                                            "sequoia.command.playerRaids.noRaidsCompleted", username)));
                            return;
                        }

                        context.getSource()
                                .sendSuccess(
                                        () -> SequoiaMod.prefix(Component.translatable(
                                                        "sequoia.command.playerRaids.showingPlayerRaids",
                                                        playerResponse.getUsername(),
                                                        playerResponse
                                                                .getGlobalData()
                                                                .getRaids()
                                                                .getTotal())
                                                .append("\n")
                                                .append(playerResponse
                                                        .getGlobalData()
                                                        .getRaids()
                                                        .toPrettyMessage(playerResponse.getRanking()))),
                                        false);
                    }
                }
            });

            context.getSource()
                    .sendSystemMessage(SequoiaMod.prefix(
                            Component.translatable("sequoia.command.playerRaids.lookingUpPlayer", username)));
        }
        return 1;
    }
}
