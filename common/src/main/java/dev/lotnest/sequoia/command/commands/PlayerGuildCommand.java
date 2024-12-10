package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.wynn.player.Player;
import dev.lotnest.sequoia.wynn.player.PlayerService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
        return base.then(Commands.argument("username", StringArgumentType.greedyString())
                        .executes(this::lookupPlayerGuild))
                .executes(this::syntaxError);
    }

    private int lookupPlayerGuild(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        CompletableFuture<Player> playerCompletableFuture = PlayerService.getPlayer(username);

        playerCompletableFuture.whenComplete((player, throwable) -> {
            if (throwable != null) {
                context.getSource()
                        .sendFailure(SequoiaMod.prefix(
                                Component.translatable("sequoia.command.playerGuild.errorLookingUpPlayer", username)));
            } else {
                if (player == null) {
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(
                                    Component.translatable("sequoia.command.playerGuild.playerNotFound", username)));
                } else {
                    if (player.getGuild() != null
                            && !StringUtils.isBlank(player.getGuild().getName())) {
                        context.getSource()
                                .sendSuccess(
                                        () -> SequoiaMod.prefix(Component.translatable(
                                                "sequoia.command.playerGuild.showingPlayerGuild",
                                                player.getUsername(),
                                                player.getGuild().getRank(),
                                                player.getGuild().getName(),
                                                player.getGuild().getPrefix())),
                                        false);
                    } else {
                        context.getSource()
                                .sendSuccess(
                                        () -> SequoiaMod.prefix(Component.translatable(
                                                "sequoia.command.playerGuild.playerNotInGuild", player.getUsername())),
                                        false);
                    }
                }
            }
        });

        context.getSource()
                .sendSystemMessage(SequoiaMod.prefix(
                        Component.translatable("sequoia.command.playerGuild.lookingUpPlayer", username)));
        return 1;
    }
}
