package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.mojang.MinecraftUtils;
import dev.lotnest.sequoia.wynn.api.player.PlayerService;
import dev.lotnest.sequoia.wynn.api.player.PlayerService;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class PlayerRaidsCommand extends Command {
    @Override
    public String getCommandName() {
        return "playerraids";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pr");
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
            PlayerService.getPlayer(username).whenComplete((playerResponse, throwable) -> {
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
                        context.getSource()
                                .sendSuccess(
                                        () -> SequoiaMod.prefix(Component.translatable(
                                                "sequoia.command.playerRaids.showingPlayerRaids",
                                                playerResponse.getUsername(),
                                                playerResponse
                                                        .getGlobalData()
                                                        .getRaids()
                                                        .getTotal())),
                                        false);
                        context.getSource()
                                .sendSuccess(
                                        () -> playerResponse
                                                .getGlobalData()
                                                .getRaids()
                                                .toPrettyMessage(playerResponse.getRanking()),
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
