package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.manager.Managers;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;

public class SequoiaCommand extends Command {
    public void registerWithCommands(
            Consumer<LiteralArgumentBuilder<CommandSourceStack>> consumer, List<Command> commands) {
        List<LiteralArgumentBuilder<CommandSourceStack>> commandBuilders = getCommandBuilders();

        // Also register all our commands as subcommands under the Sequoia command and it's aliases
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
    protected List<String> getAliases() {
        return List.of("seq");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.literal("version").executes(this::version)).executes(this::help);
    }

    private int version(CommandContext<CommandSourceStack> context) {
        MutableComponent buildText;

        if (StringUtils.isBlank(SequoiaMod.getVersion())) {
            buildText = Component.literal("Could not determine Sequoia version, please report this.")
                    .withStyle(ChatFormatting.RED);
        } else {
            buildText = Component.literal("You are running Sequoia " + SequoiaMod.getVersion())
                    .append(SequoiaMod.isDevelopmentBuild() ? " (Development build)" : "")
                    .withStyle(ChatFormatting.GREEN);
        }

        context.getSource().sendSuccess(() -> buildText, false);
        return 1;
    }

    private int help(CommandContext<CommandSourceStack> context) {
        MutableComponent text = Component.literal("Available Sequoia commands: \n")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));

        describeSequoiaSubcommand(text, "version", "Shows the version of Sequoia currently installed");

        List<Command> otherCommands = Managers.Command.getCommandInstanceSet().stream()
                .filter(command -> !(command instanceof SequoiaCommand))
                .toList();
        for (Command command : otherCommands) {
            describeCommand(text, command.getCommandName(), command.getDescription());
        }
        context.getSource().sendSuccess(() -> text, false);
        return 1;
    }

    private static void describeSequoiaSubcommand(MutableComponent text, String subCommand, String description) {
        describeCommand(text, "sequoia " + subCommand, description);
    }

    private static void describeCommand(MutableComponent text, String command, String description) {
        MutableComponent clickComponent = Component.empty();
        clickComponent.setStyle(clickComponent
                .getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command))
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, Component.literal("Click here to run this command"))));

        clickComponent.append(Component.literal("/" + command).withStyle(ChatFormatting.GREEN));
        clickComponent.append(Component.literal(" - ").withStyle(ChatFormatting.DARK_GRAY));
        clickComponent.append(Component.literal(description).withStyle(ChatFormatting.GRAY));

        text.append("\n");
        text.append(clickComponent);
    }
}
