package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.mojang.MinecraftUtils;
import dev.lotnest.sequoia.utils.TimeUtils;
import dev.lotnest.sequoia.wynn.api.guild.GuildService;
import dev.lotnest.sequoia.wynn.api.player.PlayerService;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class PlayerGuildCommand extends Command {
    @Override
    public String getCommandName() {
        return "playerguild";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pg");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(
                        Commands.argument("username", StringArgumentType.word()).executes(this::lookupPlayerGuild))
                .executes(this::syntaxError);
    }

    private int lookupPlayerGuild(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        if (StringUtils.isBlank(username) || !MinecraftUtils.isValidUsername(username)) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.invalidUsername")));
        } else {
            PlayerService.getPlayer(username).whenComplete((playerResponse, playerThrowable) -> {
                if (playerThrowable != null) {
                    SequoiaMod.error("Error looking up player: " + playerThrowable.getMessage());
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(Component.translatable(
                                    "sequoia.command.playerGuild.errorLookingUpPlayer", username)));
                } else {
                    if (playerResponse == null) {
                        context.getSource()
                                .sendFailure(SequoiaMod.prefix(Component.translatable(
                                        "sequoia.command.playerGuild.playerNotFound", username)));
                    } else {
                        if (playerResponse.getGuild() != null
                                && !StringUtils.isBlank(
                                        playerResponse.getGuild().getName())) {
                            GuildService.getGuild(playerResponse.getGuild().getName())
                                    .whenComplete((guild, guildThrowable) -> {
                                        if (guildThrowable != null) {
                                            context.getSource()
                                                    .sendFailure(SequoiaMod.prefix(Component.translatable(
                                                            "sequoia.command.playerGuild.errorLookingUpGuild",
                                                            playerResponse
                                                                    .getGuild()
                                                                    .getName())));
                                        } else {
                                            if (guild == null) {
                                                context.getSource()
                                                        .sendFailure(SequoiaMod.prefix(Component.translatable(
                                                                "sequoia.command.playerGuild.guildNotFound",
                                                                playerResponse
                                                                        .getGuild()
                                                                        .getName())));
                                            } else {
                                                String prettyTimeSinceJoin = TimeUtils.toPrettyTimeSince(
                                                        guild.getMembers().getJoined(playerResponse.getUsername()));
                                                context.getSource()
                                                        .sendSuccess(
                                                                () -> SequoiaMod.prefix(Component.translatable(
                                                                                "sequoia.command.playerGuild.showingPlayerGuild",
                                                                                playerResponse.getUsername(),
                                                                                playerResponse
                                                                                        .getGuild()
                                                                                        .getRank(),
                                                                                playerResponse
                                                                                        .getGuild()
                                                                                        .getName(),
                                                                                playerResponse
                                                                                        .getGuild()
                                                                                        .getPrefix()))
                                                                        .append(Component.literal("\n"))
                                                                        .append(SequoiaMod.prefix(
                                                                                Component.translatable(
                                                                                        "sequoia.command.playerGuild.partOfTheGuildFor",
                                                                                        prettyTimeSinceJoin))),
                                                                false);
                                            }
                                        }
                                    });
                        } else {
                            context.getSource()
                                    .sendSuccess(
                                            () -> SequoiaMod.prefix(Component.translatable(
                                                    "sequoia.command.playerGuild.playerNotInGuild",
                                                    playerResponse.getUsername())),
                                            false);
                        }
                    }
                }
            });

            context.getSource()
                    .sendSystemMessage(SequoiaMod.prefix(
                            Component.translatable("sequoia.command.playerGuild.lookingUpPlayer", username)));
        }
        return 1;
    }
}
