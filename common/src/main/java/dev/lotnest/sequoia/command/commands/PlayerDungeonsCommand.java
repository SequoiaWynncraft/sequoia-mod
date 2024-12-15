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

public class PlayerDungeonsCommand extends Command {
    @Override
    public String getCommandName() {
        return "playerdungeons";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pd");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.argument("username", StringArgumentType.greedyString())
                        .executes(this::lookupPlayerDungeons))
                .executes(this::syntaxError);
    }

    private int lookupPlayerDungeons(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        CompletableFuture<Player> playerCompletableFuture = PlayerService.getPlayer(username);

        playerCompletableFuture.whenComplete((player, throwable) -> {
            if (throwable != null) {
                context.getSource()
                        .sendFailure(SequoiaMod.prefix(Component.translatable(
                                "sequoia.command.playerDungeons.errorLookingUpPlayer", username)));
            } else {
                if (player == null
                        || player.getGlobalData() == null
                        || player.getGlobalData().getDungeons() == null) {
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(
                                    Component.translatable("sequoia.command.playerDungeons.playerNotFound", username)));
                } else {
                    context.getSource()
                            .sendSuccess(
                                    () -> SequoiaMod.prefix(Component.translatable(
                                            "sequoia.command.playerDungeons.showingPlayerDungeons",
                                            player.getUsername(),
                                            player.getGlobalData().getDungeons().getTotal())),
                                    false);
                    context.getSource()
                            .sendSuccess(
                                    () -> player.getGlobalData().getDungeons().toPrettyMessage(), false);
                }
            }
        });

        context.getSource()
                .sendSystemMessage(SequoiaMod.prefix(
                        Component.translatable("sequoia.command.playerDungeons.lookingUpPlayer", username)));
        return 1;
    }
}
