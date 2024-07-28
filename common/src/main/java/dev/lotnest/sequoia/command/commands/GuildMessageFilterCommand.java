package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.feature.features.GuildMessageFilterFeature;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class GuildMessageFilterCommand extends Command {
    @Override
    public String getCommandName() {
        return "guildmessagefilter";
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.literal("keepAll").executes(this::keepGuildMessages))
                .then(Commands.literal("cancel").executes(this::cancelGuildMessages))
                .then(Commands.literal("cancelFFA").executes(this::cancelFFAGuildMessages))
                .then(Commands.literal("grayOut").executes(this::grayOutGuildMessages))
                .then(Commands.literal("grayOutFFA").executes(this::grayOutFFAGuildMessages))
                .executes(this::syntaxError);
    }

    private int keepGuildMessages(CommandContext<CommandSourceStack> context) {
        if (GuildMessageFilterFeature.guildMessageFilterDecision
                != GuildMessageFilterFeature.GuildMessageFilterDecision.KEEP_ALL) {
            GuildMessageFilterFeature.guildMessageFilterDecision =
                    GuildMessageFilterFeature.GuildMessageFilterDecision.KEEP_ALL;
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable("sequoia.command.guildMessageFilter.keepGuildMessagesEnabled"),
                            false);
        } else {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.keepGuildMessagesAlreadyEnabled"),
                            false);
        }
        return 1;
    }

    private int cancelGuildMessages(CommandContext<CommandSourceStack> context) {
        if (GuildMessageFilterFeature.guildMessageFilterDecision
                != GuildMessageFilterFeature.GuildMessageFilterDecision.CANCEL) {
            GuildMessageFilterFeature.guildMessageFilterDecision =
                    GuildMessageFilterFeature.GuildMessageFilterDecision.CANCEL;
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.cancelGuildMessagesEnabled"),
                            false);
        } else {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.cancelGuildMessagesAlreadyEnabled"),
                            false);
        }
        return 1;
    }

    private int cancelFFAGuildMessages(CommandContext<CommandSourceStack> context) {
        if (GuildMessageFilterFeature.guildMessageFilterDecision
                != GuildMessageFilterFeature.GuildMessageFilterDecision.CANCEL_FFA) {
            GuildMessageFilterFeature.guildMessageFilterDecision =
                    GuildMessageFilterFeature.GuildMessageFilterDecision.CANCEL_FFA;
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.cancelFFAGuildMessagesEnabled"),
                            false);
        } else {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.cancelFFAGuildMessagesAlreadyEnabled"),
                            false);
        }
        return 1;
    }

    private int grayOutGuildMessages(CommandContext<CommandSourceStack> context) {
        if (GuildMessageFilterFeature.guildMessageFilterDecision
                != GuildMessageFilterFeature.GuildMessageFilterDecision.GRAY_OUT) {
            GuildMessageFilterFeature.guildMessageFilterDecision =
                    GuildMessageFilterFeature.GuildMessageFilterDecision.GRAY_OUT;
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.grayOutGuildMessagesEnabled"),
                            false);
        } else {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.grayOutGuildMessagesAlreadyEnabled"),
                            false);
        }
        return 1;
    }

    private int grayOutFFAGuildMessages(CommandContext<CommandSourceStack> context) {
        if (GuildMessageFilterFeature.guildMessageFilterDecision
                != GuildMessageFilterFeature.GuildMessageFilterDecision.GRAY_OUT_FFA) {
            GuildMessageFilterFeature.guildMessageFilterDecision =
                    GuildMessageFilterFeature.GuildMessageFilterDecision.GRAY_OUT_FFA;
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.grayOutFFAGuildMessagesEnabled"),
                            false);
        } else {
            context.getSource()
                    .sendSuccess(
                            () -> Component.translatable(
                                    "sequoia.command.guildMessageFilter.grayOutFFAGuildMessagesAlreadyEnabled"),
                            false);
        }
        return 1;
    }
}
