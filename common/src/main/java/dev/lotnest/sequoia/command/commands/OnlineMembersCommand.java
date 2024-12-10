package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.wynn.guild.Guild;
import dev.lotnest.sequoia.wynn.guild.GuildService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class OnlineMembersCommand extends Command {
    @Override
    public String getCommandName() {
        return "onlinemembers";
    }

    @Override
    public List<String> getAliases() {
        return List.of("om");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.argument("guildName", StringArgumentType.greedyString())
                        .executes(this::lookupGuild))
                .executes(this::syntaxError);
    }

    private int lookupGuild(CommandContext<CommandSourceStack> context) {
        String guildName = context.getArgument("guildName", String.class);
        CompletableFuture<Guild> completableFuture = GuildService.getGuild(guildName);

        completableFuture.whenComplete((guild, throwable) -> {
            if (throwable != null) {
                SequoiaMod.error("Error looking up " + guildName + "'s guild members", throwable);
                context.getSource()
                        .sendFailure(SequoiaMod.prefix(Component.translatable(
                                "sequoia.command.onlineMembers.errorLookingUpGuildMembers", guildName)));
            } else {
                if (guild == null) {
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(
                                    Component.translatable("sequoia.command.onlineMembers.guildNotFound", guildName)));
                } else {
                    context.getSource()
                            .sendSuccess(
                                    () -> SequoiaMod.prefix(Component.translatable(
                                            "sequoia.command.onlineMembers.showingOnlineGuildMembers",
                                            guild.getName(),
                                            guild.getPrefix(),
                                            guild.getOnline(),
                                            guild.getMembers().getTotal())),
                                    false);
                    context.getSource()
                            .sendSuccess(() -> guild.getOnlineMembers().toPrettyMessage(), false);
                }
            }
        });

        context.getSource()
                .sendSystemMessage(SequoiaMod.prefix(
                        Component.translatable("sequoia.command.onlineMembers.lookingUpGuildMembers", guildName)));
        return 1;
    }
}
