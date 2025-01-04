package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;

public class VersionCommand extends Command {
    @Override
    public String getCommandName() {
        return "version";
    }

    @Override
    public List<String> getAliases() {
        return List.of("v");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::version);
    }

    private int version(CommandContext<CommandSourceStack> context) {
        MutableComponent versionMessage;

        if (StringUtils.isBlank(SequoiaMod.getVersion())) {
            versionMessage =
                    SequoiaMod.prefix(Component.literal("Could not determine Sequoia version, please report this.")
                            .withStyle(ChatFormatting.RED));
        } else {
            versionMessage = SequoiaMod.prefix(Component.literal("You are running Sequoia " + SequoiaMod.getVersion())
                    .append(SequoiaMod.isDevelopmentBuild() ? " (Development build)" : "")
                    .append(
                            " by Lotnest (Owner & Lead Developer), Iriya_ (Mod Developer), dotJJ (Server Developer) and OwORawr (Sequoia OST composer).")
                    .withStyle(ChatFormatting.GREEN));
        }

        McUtils.sendMessageToClient(versionMessage);
        return 1;
    }
}
