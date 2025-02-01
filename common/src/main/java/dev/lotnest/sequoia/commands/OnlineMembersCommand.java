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
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class OnlineMembersCommand extends Command {
    @Override
    public String getCommandName() {
        return "onlineMembers";
    }

    @Override
    public List<String> getAliases() {
        return List.of("om");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.argument("guildName", StringArgumentType.word())
                        .executes(this::lookupGuild))
                .executes(this::syntaxError);
    }

    private int lookupGuild(CommandContext<CommandSourceStack> context) {
        String guildName = context.getArgument("guildName", String.class);
        if (StringUtils.isBlank(guildName) || !guildName.matches("^[a-zA-Z0-9_ ]+$")) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(
                            Component.translatable("sequoia.command.onlineMembers.invalidGuildName")));
        } else {
            Services.Guild.getGuild(guildName).whenComplete((guildResponse, throwable) -> {
                if (throwable != null) {
                    SequoiaMod.error("Error looking up " + guildName + "'s guild members", throwable);
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(Component.translatable(
                                    "sequoia.command.onlineMembers.errorLookingUpGuildMembers", guildName)));
                } else {
                    if (guildResponse == null) {
                        context.getSource()
                                .sendFailure(SequoiaMod.prefix(Component.translatable(
                                        "sequoia.command.onlineMembers.guildNotFound", guildName)));
                    } else {
                        context.getSource()
                                .sendSuccess(
                                        () -> SequoiaMod.prefix(Component.translatable(
                                                        "sequoia.command.onlineMembers.showingGuildMembers",
                                                        guildResponse.getName(),
                                                        guildResponse.getPrefix(),
                                                        guildResponse.getOnline(),
                                                        guildResponse
                                                                .getMembers()
                                                                .getTotal())
                                                .append(Component.literal("\n"))
                                                .append(
                                                        guildResponse.getOnline() > 0
                                                                ? guildResponse
                                                                        .getOnlineMembers()
                                                                        .toPrettyMessage()
                                                                : Component.empty())),
                                        false);
                    }
                }
            });

            context.getSource()
                    .sendSystemMessage(SequoiaMod.prefix(
                            Component.translatable("sequoia.command.onlineMembers.lookingUpGuildMembers", guildName)));
        }
        return 1;
    }
}
