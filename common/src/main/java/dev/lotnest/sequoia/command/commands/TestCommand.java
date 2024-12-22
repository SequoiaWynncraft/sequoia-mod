package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.text.FormattedTextMessageParser;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TestCommand extends Command {
    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.literal(FormattedTextMessageParser.class.getSimpleName())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(this::testFormattedTextMessageParser)))
                .executes(this::syntaxError);
    }

    private int testFormattedTextMessageParser(CommandContext<CommandSourceStack> context) {
        String message = context.getArgument("message", String.class);
        context.getSource().sendSuccess(() -> FormattedTextMessageParser.buildFromString(message), false);
        return 1;
    }
}
