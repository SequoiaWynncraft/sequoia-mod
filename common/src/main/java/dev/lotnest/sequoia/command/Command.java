package dev.lotnest.sequoia.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.component.Translatable;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public abstract class Command implements Translatable {
    public abstract String getCommandName();

    public List<String> getAliases() {
        return List.of();
    }

    public String getDescription() {
        return getTranslation("description");
    }

    @Override
    public String getTypeName() {
        return "Command";
    }

    protected abstract LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base);

    public final List<LiteralArgumentBuilder<CommandSourceStack>> getCommandBuilders() {
        return Stream.concat(
                        Stream.of(Commands.literal(getCommandName())),
                        getAliases().stream().map(Commands::literal))
                .map(this::getCommandBuilder)
                .toList();
    }

    protected final int syntaxError(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(SequoiaMod.prefix(Component.translatable("sequoia.command.syntaxError")));
        return 1;
    }
}
