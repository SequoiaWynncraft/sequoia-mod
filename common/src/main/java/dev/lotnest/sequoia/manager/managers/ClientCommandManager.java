package dev.lotnest.sequoia.manager.managers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.wynntils.mc.event.CommandsAddedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.ClientCommandSourceStack;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.command.commands.ConfigCommand;
import dev.lotnest.sequoia.command.commands.ConnectCommand;
import dev.lotnest.sequoia.command.commands.DisconnectCommand;
import dev.lotnest.sequoia.command.commands.DiscordCommand;
import dev.lotnest.sequoia.command.commands.LastSeenCommand;
import dev.lotnest.sequoia.command.commands.MeowCommand;
import dev.lotnest.sequoia.command.commands.OnlineMembersCommand;
import dev.lotnest.sequoia.command.commands.OuterVoidCommand;
import dev.lotnest.sequoia.command.commands.PlayerDungeonsCommand;
import dev.lotnest.sequoia.command.commands.PlayerGuildCommand;
import dev.lotnest.sequoia.command.commands.PlayerRaidsCommand;
import dev.lotnest.sequoia.command.commands.PlayerWarsCommand;
import dev.lotnest.sequoia.command.commands.ReconnectCommand;
import dev.lotnest.sequoia.command.commands.SequoiaCommand;
import dev.lotnest.sequoia.command.commands.TestCommand;
import dev.lotnest.sequoia.manager.Manager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

// Credits to Earthcomputer and Forge
// Parts of this code originates from https://github.com/Earthcomputer/clientcommands, and other
// parts originate from https://github.com/MinecraftForge/MinecraftForge
// Kudos to both of the above

/**
 * We register our commands in two significant ways:
 * <ol>
 *     <li> Registering them to our custom client dispatcher,
 *     which is used to parse and execute commands.
 *     <li> Registering them to the server dispatcher, which is used to suggest commands.
 *     This is done after the server initializes the dispatcher.
 * </ol>
 */
public final class ClientCommandManager extends Manager {
    private final CommandDispatcher<CommandSourceStack> clientDispatcher = new CommandDispatcher<>();

    private final List<Command> commandInstanceSet = Lists.newArrayList();
    private SequoiaCommand sequoiaCommand;

    public ClientCommandManager() {
        super(List.of());

        registerAllCommands();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommandsAdded(CommandsAddedEvent event) {
        for (Command command : commandInstanceSet) {
            command.getCommandBuilders().stream()
                    .map(LiteralArgumentBuilder::build)
                    .forEach(node -> addNode(event.getRoot(), node));
        }

        sequoiaCommand.registerWithCommands(builder -> addNode(event.getRoot(), builder.build()), commandInstanceSet);
    }

    @SuppressWarnings("unchecked")
    public void addNode(
            RootCommandNode<SharedSuggestionProvider> root, CommandNode<? extends SharedSuggestionProvider> node) {
        root.addChild((LiteralCommandNode<SharedSuggestionProvider>) node);
    }

    public void addNodeToClientDispatcher(LiteralArgumentBuilder<CommandSourceStack> nodeBuilder) {
        clientDispatcher.register(nodeBuilder);
    }

    public boolean handleCommand(String message) {
        StringReader reader = new StringReader(message);
        return executeCommand(reader, message);
    }

    private boolean executeCommand(StringReader reader, String command) {
        ClientCommandSourceStack source = getSource();

        if (source == null) return false;

        final ParseResults<CommandSourceStack> parse = clientDispatcher.parse(reader, source);

        if (!parse.getExceptions().isEmpty()
                || (parse.getContext().getCommand() == null
                && parse.getContext().getChild() == null)) {
            return false;
        }

        try {
            clientDispatcher.execute(parse);
        } catch (CommandSyntaxException exception) {
            McUtils.sendErrorToClient(exception.getRawMessage().getString());
            if (exception.getInput() != null && exception.getCursor() >= 0) {
                int cursor =
                        Math.min(exception.getCursor(), exception.getInput().length());
                MutableComponent text = Component.literal("")
                        .withStyle(Style.EMPTY
                                .withColor(ChatFormatting.GRAY)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (cursor > 10) text.append("...");

                text.append(exception.getInput().substring(Math.max(0, cursor - 10), cursor));
                if (cursor < exception.getInput().length()) {
                    text.append(Component.literal(exception.getInput().substring(cursor))
                            .withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE));
                }

                text.append(Component.translatable("command.context.here")
                        .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                sendError(text);
            }
        } catch (RuntimeException exception) {
            MutableComponent error = Component.literal(
                    exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            sendError(Component.translatable("command.failed")
                    .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error))));
            SequoiaMod.error("Failed to execute command", exception);
        }

        return true;
    }

    private ClientCommandSourceStack getSource() {
        LocalPlayer player = McUtils.player();

        if (player == null) return null;

        return new ClientCommandSourceStack(player);
    }

    private void sendError(MutableComponent error) {
        McUtils.sendMessageToClient(error.withStyle(ChatFormatting.RED));
    }

    public List<Command> getCommandInstanceSet() {
        return commandInstanceSet;
    }

    private void registerCommand(Command command) {
        commandInstanceSet.add(command);
        command.getCommandBuilders().forEach(clientDispatcher::register);
    }

    private void registerCommandWithCommandSet(SequoiaCommand sequoiaCommand) {
        sequoiaCommand.registerWithCommands(clientDispatcher::register, commandInstanceSet);
        this.sequoiaCommand = sequoiaCommand;
    }

    private void registerAllCommands() {
        registerCommand(new ConfigCommand());
        registerCommand(new OnlineMembersCommand());
        registerCommand(new MeowCommand());
        registerCommand(new LastSeenCommand());
        registerCommand(new PlayerGuildCommand());
        registerCommand(new PlayerRaidsCommand());
        registerCommand(new PlayerDungeonsCommand());
        registerCommand(new TestCommand());
        registerCommand(new PlayerWarsCommand());
        registerCommand(new DiscordCommand());
//        registerCommand(new SearchCommand());
        registerCommand(new ConnectCommand());
        registerCommand(new DisconnectCommand());
        registerCommand(new ReconnectCommand());
        registerCommand(new OuterVoidCommand());

        registerCommandWithCommandSet(new SequoiaCommand());
    }
}
