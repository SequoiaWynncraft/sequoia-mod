package dev.lotnest.sequoia.command.commands;

import com.google.common.collect.Sets;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.feature.features.RevealNicknamesFeature;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class IgnorePlayerCommand extends Command {
    private static final Pattern GUILD_CHAT_PATTERN =
            Pattern.compile("§3\\[§b(?<stars>★{0,5})?§3<(?<player>.+?)>§r§3]§b (?<message>.+)");

    private static final Pattern SHOUT_PATTERN =
            Pattern.compile("§5<(?<player>.+?)> §r§b\\[WC(?<number>\\d+)] shouts: §d(?<message>.+)");

    private static final Pattern PARTY_PATTERN = Pattern.compile("§7\\[§e(?<player>.+?)§7] §f(?<message>.+)");

    // This is only for testing purposes, the actual implementation will get saved in a file
    private static final Set<String> ignoredPlayers = Sets.newHashSet();

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
        context.getSource()
                .sendSystemMessage(
                        Component.literal("This doesn't really work yet.").withStyle(ChatFormatting.RED));

        String username = context.getArgument("username", String.class);
        if (ignoredPlayers.add(username)) {
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
        if (ignoredPlayers.remove(username)) {
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
        if (ignoredPlayers.isEmpty()) {
            context.getSource()
                    .sendSuccess(() -> Component.translatable("sequoia.command.ignorePlayer.noIgnoredPlayers"), false);
            return 1;
        }

        context.getSource()
                .sendSuccess(
                        () -> Component.translatable(
                                "sequoia.command.ignorePlayer.listingIgnoredPlayers",
                                String.join(", ", ignoredPlayers)),
                        false);
        return 1;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMessage(ChatMessageReceivedEvent event) {
        String username =
                RevealNicknamesFeature.revealNickname(event.getStyledText()).getStringWithoutFormatting();

        Matcher guildMatcher = GUILD_CHAT_PATTERN.matcher(event.getMessage().getString());
        if (guildMatcher.matches()) {
            if (StringUtils.isBlank(username)) {
                username = guildMatcher.group("player");
            }

            if (ignoredPlayers.contains(username)) {
                event.setCanceled(true);
            }

            return;
        }

        Matcher shoutMatcher = SHOUT_PATTERN.matcher(event.getMessage().getString());
        if (shoutMatcher.matches()) {
            if (StringUtils.isBlank(username)) {
                username = shoutMatcher.group("player");
            }

            if (ignoredPlayers.contains(username)) {
                event.setCanceled(true);
            }

            return;
        }

        Matcher partyMatcher = PARTY_PATTERN.matcher(event.getMessage().getString());
        if (partyMatcher.matches()) {
            if (StringUtils.isBlank(username)) {
                username = partyMatcher.group("player");
            }

            if (ignoredPlayers.contains(username)) {
                event.setCanceled(true);
            }
        }
    }
}
