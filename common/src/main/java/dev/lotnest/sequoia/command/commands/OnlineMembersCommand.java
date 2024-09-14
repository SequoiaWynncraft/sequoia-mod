package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.wynntils.core.components.Models;
import com.wynntils.models.players.type.GuildInfo;
import com.wynntils.models.players.type.GuildMemberInfo;
import com.wynntils.models.players.type.GuildRank;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class OnlineMembersCommand extends Command {
    private static final SuggestionProvider<CommandSourceStack> GUILD_SUGGESTION_PROVIDER =
            (context, builder) -> SharedSuggestionProvider.suggest(Models.Guild.getAllGuilds(), builder);

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
                        .suggests(GUILD_SUGGESTION_PROVIDER)
                        .executes(this::lookupGuild))
                .executes(this::syntaxError);
    }

    private int lookupGuild(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.translatable("sequoia.command.onlineMembers.lookingUp"), false);

        CompletableFuture<GuildInfo> guildInfoCompletableFuture =
                Models.Guild.getGuild(context.getArgument("guildName", String.class));

        guildInfoCompletableFuture.whenComplete((guild, throwable) -> {
            if (throwable != null) {
                McUtils.sendMessageToClient(Component.literal(
                                "Unable to view online members for " + context.getArgument("guildName", String.class))
                        .withStyle(ChatFormatting.RED));
                SequoiaMod.error("Error trying to parse guild online members", throwable);
            } else {
                if (guild == null) {
                    McUtils.sendMessageToClient(
                            Component.literal("Unknown guild " + context.getArgument("guildName", String.class))
                                    .withStyle(ChatFormatting.RED));
                    return;
                }

                MutableComponent response = Component.literal(guild.name() + " [" + guild.prefix() + "]")
                        .withStyle(ChatFormatting.DARK_AQUA);

                response.append(Component.literal(" has ").withStyle(ChatFormatting.GRAY));

                response.append(Component.literal(guild.onlineMembers() + "/" + guild.totalMembers())
                                .withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(" members currently online:").withStyle(ChatFormatting.GRAY));

                List<GuildRank> guildRanks = Arrays.asList(GuildRank.values());
                // Reversed so online members are sorted from most to least important
                Collections.reverse(guildRanks);

                for (GuildRank guildRank : guildRanks) {
                    List<GuildMemberInfo> onlineRankMembers = guild.getOnlineMembersbyRank(guildRank);

                    if (!onlineRankMembers.isEmpty()) {
                        response.append(Component.literal("\n" + guildRank.getGuildDescription() + ":\n")
                                .withStyle(ChatFormatting.GOLD));

                        for (GuildMemberInfo guildMember : onlineRankMembers) {
                            response.append(Component.literal(guildMember.username())
                                    .withStyle(ChatFormatting.AQUA)
                                    .withStyle(style -> style.withHoverEvent(new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT,
                                                    Component.literal("Click to private message "
                                                                    + guildMember.username())
                                                            .withStyle(ChatFormatting.GRAY)))
                                            .withClickEvent(new ClickEvent(
                                                    ClickEvent.Action.SUGGEST_COMMAND,
                                                    "/msg " + guildMember.username() + " "))));

                            if (onlineRankMembers.indexOf(guildMember) != onlineRankMembers.size() - 1) {
                                response.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                            }
                        }
                    }
                }

                McUtils.sendMessageToClient(response);
            }
        });

        return 1;
    }
}
