package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.utils.TimeUtils;
import dev.lotnest.sequoia.wynn.api.player.PlayerResponse;
import dev.lotnest.sequoia.wynn.api.player.PlayerService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.apache.commons.lang3.StringUtils;

public class LastSeenCommand extends Command {
    @Override
    public String getCommandName() {
        return "lastseen";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ls");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.argument("username", StringArgumentType.greedyString())
                        .executes(this::lookupPlayerLastSeen))
                .executes(this::syntaxError);
    }

    private int lookupPlayerLastSeen(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        if (StringUtils.isBlank(username) || !username.matches("^[a-zA-Z0-9_]+$")) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.invalidPlayerName")));
        } else {
            CompletableFuture<PlayerResponse> playerCompletableFuture = PlayerService.getPlayer(username);
            playerCompletableFuture.whenComplete((playerResponse, throwable) -> {
                if (throwable != null) {
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(
                                    Component.translatable("sequoia.command.lastSeen.errorLookingUpPlayer", username)));
                } else {
                    if (playerResponse == null) {
                        context.getSource()
                                .sendFailure(SequoiaMod.prefix(
                                        Component.translatable("sequoia.command.lastSeen.playerNotFound", username)));
                    } else {
                        if (playerResponse.isOnline()) {
                            context.getSource()
                                    .sendSuccess(
                                            () -> SequoiaMod.prefix(Component.translatable(
                                                            "sequoia.command.lastSeen.showingPlayerLastSeenOnline",
                                                            playerResponse.getUsername(),
                                                            playerResponse.getServer())
                                                    .withStyle(style -> style.withHoverEvent(new HoverEvent(
                                                                    HoverEvent.Action.SHOW_TEXT,
                                                                    Component.translatable(
                                                                            "sequoia.tooltip.clickToPrivateMessage",
                                                                            playerResponse.getUsername())))
                                                            .withClickEvent(new ClickEvent(
                                                                    ClickEvent.Action.SUGGEST_COMMAND,
                                                                    "/msg " + playerResponse.getUsername() + " ")))),
                                            false);
                        } else {
                            context.getSource()
                                    .sendSuccess(
                                            () -> SequoiaMod.prefix(Component.translatable(
                                                    "sequoia.command.lastSeen.showingPlayerLastSeenOffline",
                                                    playerResponse.getUsername(),
                                                    TimeUtils.toPrettyTimeSince(playerResponse.getLastJoin()))),
                                            false);
                        }
                    }
                }
            });

            context.getSource()
                    .sendSystemMessage(SequoiaMod.prefix(
                            Component.translatable("sequoia.command.lastSeen.lookingUpPlayer", username)));
        }
        return 1;
    }
}
