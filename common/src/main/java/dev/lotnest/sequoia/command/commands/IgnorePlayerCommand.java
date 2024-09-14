package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class IgnorePlayerCommand extends Command {
    @Override
    public String getCommandName() {
        return "ignoreplayer";
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.literal("add")
                        .then(Commands.argument("username", StringArgumentType.word())
                                .executes(this::addPlayerToIgnoreList)))
                .then(Commands.literal("remove")
                        .then(Commands.argument("username", StringArgumentType.word())
                                .executes(this::removePlayerFromIgnoreList)))
                .then(Commands.literal("list").executes(this::listIgnoredPlayers))
                .executes(this::syntaxError);
    }

    private int addPlayerToIgnoreList(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        if (!SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers().contains(username)) {
            SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers().add(username);
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable("sequoia.command.ignorePlayer.playerIgnored", username),
                            false);
        } else {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable("sequoia.command.ignorePlayer.playerAlreadyIgnored", username),
                            false);
        }
        return 1;
    }

    private int removePlayerFromIgnoreList(CommandContext<CommandSourceStack> context) {
        String username = context.getArgument("username", String.class);
        if (SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers().contains(username)) {
            SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers().remove(username);
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable("sequoia.command.ignorePlayer.playerUnignored", username),
                            false);
        } else {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable("sequoia.command.ignorePlayer.playerNotIgnored", username),
                            false);
        }
        return 1;
    }

    private int listIgnoredPlayers(CommandContext<CommandSourceStack> context) {
        if (SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers().isEmpty()) {
            context.getSource()
                    .sendSuccess(() -> Component.translatable("sequoia.command.ignorePlayer.noIgnoredPlayers"), false);
        } else {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.ignorePlayer.listingIgnoredPlayers",
                                    String.join(", ", SequoiaMod.CONFIG.playerIgnoreFeature.ignoredPlayers())),
                            false);
        }
        return 1;
    }
}
