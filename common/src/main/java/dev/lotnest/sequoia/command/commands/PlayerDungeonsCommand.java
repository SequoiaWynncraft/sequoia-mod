package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.wynn.api.player.PlayerService;
import dev.lotnest.sequoia.mojang.MinecraftUtils;
import dev.lotnest.sequoia.wynn.api.player.PlayerService;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

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
        return base.then(
                        Commands.argument("username", StringArgumentType.word()).executes(this::lookupPlayerDungeons))
                .executes(this::syntaxError);
    }

    private int lookupPlayerDungeons(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        if (StringUtils.isBlank(username) || !MinecraftUtils.isValidUsername(username)) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.invalidUsername")));
        } else {
            PlayerService.getPlayer(username).whenComplete((playerResponse, throwable) -> {
                if (throwable != null) {
                    SequoiaMod.error("Error looking up player: " + username, throwable);
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(Component.translatable(
                                    "sequoia.command.playerDungeons.errorLookingUpPlayer", username)));
                } else {
                    if (playerResponse == null
                            || playerResponse.getGlobalData() == null
                            || playerResponse.getGlobalData().getDungeons() == null) {
                        context.getSource()
                                .sendFailure(SequoiaMod.prefix(Component.translatable(
                                        "sequoia.command.playerDungeons.playerNotFound", username)));
                    } else {
                        context.getSource()
                                .sendSuccess(
                                        () -> SequoiaMod.prefix(Component.translatable(
                                                "sequoia.command.playerDungeons.showingPlayerDungeons",
                                                playerResponse.getUsername(),
                                                playerResponse
                                                        .getGlobalData()
                                                        .getDungeons()
                                                        .getTotal())),
                                        false);
                        context.getSource()
                                .sendSuccess(
                                        () -> playerResponse
                                                .getGlobalData()
                                                .getDungeons()
                                                .toPrettyMessage(),
                                        false);
                    }
                }
            });

            context.getSource()
                    .sendSystemMessage(SequoiaMod.prefix(
                            Component.translatable("sequoia.command.playerDungeons.lookingUpPlayer", username)));
        }
        return 1;
    }
}
