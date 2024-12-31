package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.manager.Managers;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class SequoiaCommand extends Command {
    public void registerWithCommands(
            Consumer<LiteralArgumentBuilder<CommandSourceStack>> consumer, List<Command> commands) {
        List<LiteralArgumentBuilder<CommandSourceStack>> commandBuilders = getCommandBuilders();

        for (LiteralArgumentBuilder<CommandSourceStack> builder : commandBuilders) {
            for (Command commandInstance : commands) {
                if (commandInstance == this) continue;

                commandInstance.getCommandBuilders().forEach(builder::then);
            }

            consumer.accept(builder);
        }
    }

    @Override
    public String getCommandName() {
        return "sequoia";
    }

    @Override
    public List<String> getAliases() {
        return List.of("seq");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::help);
    }

    private int help(CommandContext<CommandSourceStack> context) {
        MutableComponent helpMessage = SequoiaMod.prefix(Component.literal("Available Sequoia commands: \n")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));

        List<Command> commands = Managers.Command.getCommandInstanceSet().stream()
                .filter(command -> !(command instanceof SequoiaCommand))
                .toList();
        for (Command command : commands) {
            describeSequoiaSubcommand(
                    helpMessage,
                    command.getCommandName()
                            + (command.getAliases().isEmpty() ? "" : " | " + String.join(" |", command.getAliases())),
                    command.getDescription());
        }

        McUtils.sendMessageToClient(helpMessage);
        return 1;
    }

    private static void describeSequoiaSubcommand(MutableComponent text, String subCommand, String description) {
        describeCommand(text, "seq " + subCommand, description);
    }

    private static void describeCommand(MutableComponent text, String command, String description) {
        MutableComponent clickComponent = Component.empty();
        clickComponent.setStyle(clickComponent
                .getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command))
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, Component.literal("Click here to run this command"))));

        clickComponent.append(Component.literal("/" + command).withStyle(ChatFormatting.DARK_GREEN));
        clickComponent.append(Component.literal(" - ").withStyle(ChatFormatting.YELLOW));
        clickComponent.append(Component.literal(description).withStyle(ChatFormatting.YELLOW));

        text.append("\n");
        text.append(clickComponent);
    }
}
