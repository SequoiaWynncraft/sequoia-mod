package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import io.wispforest.owo.config.ui.ConfigScreen;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;

public class ConfigCommand extends Command {
    @Override
    public String getCommandName() {
        return "config";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cf");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::openConfigGUI);
    }

    private int openConfigGUI(CommandContext<CommandSourceStack> context) {
        try {
            Class<?> configScreenProvidersClass = Class.forName("io.wispforest.owo.config.ui.ConfigScreenProviders");
            Method getMethod = configScreenProvidersClass.getMethod("get", String.class);
            Function<Object, ConfigScreen> configScreenProvider =
                    (Function<Object, ConfigScreen>) getMethod.invoke(null, SequoiaMod.MOD_ID);

            if (configScreenProvider == null) {
                SequoiaMod.error("No ConfigScreenProvider found for mod ID: " + SequoiaMod.MOD_ID);
                return 1;
            }

            if (configScreenProvider != null) {
                Object screen = configScreenProvider.apply(null);
                if (screen instanceof Screen) {
                    SequoiaMod.debug("Attempting to open ConfigScreen for mod ID: " + SequoiaMod.MOD_ID);
                    Executors.newSingleThreadScheduledExecutor()
                            .schedule(
                                    () -> Minecraft.getInstance().execute(() -> Minecraft.getInstance()
                                            .setScreen((Screen) screen)),
                                    1,
                                    TimeUnit.MILLISECONDS);
                } else {
                    SequoiaMod.error("ConfigScreenProvider returned unexpected Screen type: "
                            + screen.getClass().getName());
                }
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to open ConfigScreen", exception);
        }

        return 1;
    }
}
