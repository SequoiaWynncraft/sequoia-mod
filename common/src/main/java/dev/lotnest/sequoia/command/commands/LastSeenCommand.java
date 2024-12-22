package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.mojang.MinecraftUtils;
import dev.lotnest.sequoia.utils.TimeUtils;
import dev.lotnest.sequoia.wynn.player.Player;
import dev.lotnest.sequoia.wynn.player.PlayerService;
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
        return base.then(
                        Commands.argument("username", StringArgumentType.word()).executes(this::lookupPlayerLastSeen))
                .executes(this::syntaxError);
    }

    private int lookupPlayerLastSeen(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        if (StringUtils.isBlank(username) || !MinecraftUtils.isValidUsername(username)) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.invalidUsername")));
        } else {
            CompletableFuture<Player> playerCompletableFuture = PlayerService.getPlayer(username);
            playerCompletableFuture.whenComplete((player, throwable) -> {
                if (throwable != null) {
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(
                                    Component.translatable("sequoia.command.lastSeen.errorLookingUpPlayer", username)));
                } else {
                    if (player == null) {
                        context.getSource()
                                .sendFailure(SequoiaMod.prefix(
                                        Component.translatable("sequoia.command.lastSeen.playerNotFound", username)));
                    } else {
                        if (player.isOnline()) {
                            context.getSource()
                                    .sendSuccess(
                                            () -> SequoiaMod.prefix(Component.translatable(
                                                            "sequoia.command.lastSeen.showingPlayerLastSeenOnline",
                                                            player.getUsername(),
                                                            player.getServer())
                                                    .withStyle(style -> style.withHoverEvent(new HoverEvent(
                                                                    HoverEvent.Action.SHOW_TEXT,
                                                                    Component.translatable(
                                                                            "sequoia.tooltip.clickToPrivateMessage",
                                                                            player.getUsername())))
                                                            .withClickEvent(new ClickEvent(
                                                                    ClickEvent.Action.SUGGEST_COMMAND,
                                                                    "/msg " + player.getUsername() + " ")))),
                                            false);
                        } else {
                            context.getSource()
                                    .sendSuccess(
                                            () -> SequoiaMod.prefix(Component.translatable(
                                                    "sequoia.command.lastSeen.showingPlayerLastSeenOffline",
                                                    player.getUsername(),
                                                    TimeUtils.toPrettyTimeSince(player.getLastJoin()))),
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
