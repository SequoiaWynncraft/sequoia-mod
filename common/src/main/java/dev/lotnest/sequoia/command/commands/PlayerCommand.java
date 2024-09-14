package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.wynntils.core.components.Models;
import com.wynntils.utils.DateFormatter;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PlayerCommand extends Command {
    private static final DateFormatter DATE_FORMATTER = new DateFormatter(true);
    private static final SuggestionProvider<CommandSourceStack> PLAYER_NAME_SUGGESTION_PROVIDER =
            (context, builder) -> SharedSuggestionProvider.suggest(Models.Player.getAllPlayerNames(), builder);

    @Override
    public String getCommandName() {
        return "player";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.literal("guild")
                        .then(Commands.argument("username", StringArgumentType.word())
                                .suggests(PLAYER_NAME_SUGGESTION_PROVIDER)
                                .executes(this::lookupPlayerGuild)))
                .then(Commands.literal("g")
                        .then(Commands.argument("username", StringArgumentType.word())
                                .suggests(PLAYER_NAME_SUGGESTION_PROVIDER)
                                .executes(this::lookupPlayerGuild)))
                .then(Commands.literal("lastSeen")
                        .then(Commands.argument("username", StringArgumentType.word())
                                .suggests(PLAYER_NAME_SUGGESTION_PROVIDER)
                                .executes(this::lookupPlayerLastSeen)))
                .then(Commands.literal("ls")
                        .then(Commands.argument("username", StringArgumentType.word())
                                .suggests(PLAYER_NAME_SUGGESTION_PROVIDER)
                                .executes(this::lookupPlayerLastSeen)))
                .then(Commands.literal("armor")
                        .then(Commands.argument("username", StringArgumentType.word())
                                .suggests(PLAYER_NAME_SUGGESTION_PROVIDER)
                                .executes(this::lookupPlayerArmor)))
                .executes(this::syntaxError);
    }

    private int lookupPlayerGuild(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.translatable("sequoia.command.player.lookingUp"), false);

        Models.Player.getPlayer(context.getArgument("username", String.class)).whenComplete((player, throwable) -> {
            if (throwable != null) {
                McUtils.sendMessageToClient(Component.literal(
                                "Unable to view player guild for " + context.getArgument("username", String.class))
                        .withStyle(ChatFormatting.RED));
                SequoiaMod.error("Error trying to parse player guild", throwable);
            } else {
                if (player == null) {
                    McUtils.sendMessageToClient(
                            Component.literal("Unknown player " + context.getArgument("username", String.class))
                                    .withStyle(ChatFormatting.RED));
                    return;
                }

                MutableComponent response = Component.literal(player.username()).withStyle(ChatFormatting.DARK_AQUA);

                if (player.guildName() != null) {
                    response.append(Component.literal(" is a ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(player.guildRank().getGuildDescription())
                                    .withStyle(ChatFormatting.AQUA)
                                    .append(Component.literal(" of ")
                                            .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal(
                                                            player.guildName() + " [" + player.guildPrefix() + "]")
                                                    .withStyle(ChatFormatting.AQUA)))));

                    // Should only be null if the player lookup succeeded but the guild lookup did not
                    if (player.guildJoinTimestamp() != null) {
                        long differenceInMillis = System.currentTimeMillis()
                                - player.guildJoinTimestamp().toEpochMilli();

                        response.append(Component.literal("\nThey have been in the guild for ")
                                .withStyle(ChatFormatting.GRAY)
                                .append(Component.literal(DATE_FORMATTER.format(differenceInMillis))
                                        .withStyle(ChatFormatting.AQUA)));
                    }
                } else {
                    response.append(Component.literal(" is not in a guild").withStyle(ChatFormatting.GRAY));
                }

                McUtils.sendMessageToClient(response);
            }
        });

        return 1;
    }

    private int lookupPlayerLastSeen(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.translatable("sequoia.command.player.lookingUp"), false);

        Models.Player.getPlayer(context.getArgument("username", String.class)).whenComplete((player, throwable) -> {
            if (throwable != null) {
                McUtils.sendMessageToClient(Component.literal(
                                "Unable to view player last seen for " + context.getArgument("username", String.class))
                        .withStyle(ChatFormatting.RED));
                SequoiaMod.error("Error trying to parse player last seen", throwable);
            } else {
                if (player == null) {
                    McUtils.sendMessageToClient(
                            Component.literal("Unknown player " + context.getArgument("username", String.class))
                                    .withStyle(ChatFormatting.RED));
                    return;
                }

                MutableComponent response = Component.literal(player.username()).withStyle(ChatFormatting.AQUA);

                if (player.online()) {
                    response.append(Component.literal(" is online on ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(player.server()).withStyle(ChatFormatting.GOLD)));
                } else {
                    long differenceInMillis = System.currentTimeMillis()
                            - player.lastJoinTimestamp().toEpochMilli();

                    response.append(Component.literal(" was last seen ").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(DATE_FORMATTER.format(differenceInMillis))
                                    .withStyle(ChatFormatting.GOLD)
                                    .append(Component.literal("ago").withStyle(ChatFormatting.GRAY)));
                }

                McUtils.sendMessageToClient(response);
            }
        });

        return 1;
    }

    private int lookupPlayerArmor(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        McUtils.sendMessageToClient(Component.literal("TEST: Your armor:\n")
                .append(McUtils.player().getInventory().getArmor(0).getDisplayName())
                .append(Component.literal(" "))
                .append(McUtils.player().getInventory().getArmor(1).getDisplayName())
                .append(Component.literal(" "))
                .append(McUtils.player().getInventory().getArmor(2).getDisplayName())
                .append(Component.literal(" "))
                .append(McUtils.player().getInventory().getArmor(3).getDisplayName()));
        return 1;
    }
}
