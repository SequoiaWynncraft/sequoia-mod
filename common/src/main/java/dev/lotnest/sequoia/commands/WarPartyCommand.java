/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Managers;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Models;
import dev.lotnest.sequoia.core.consumers.command.Command;
import dev.lotnest.sequoia.mc.screens.WarPartyScreen;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class WarPartyCommand extends Command {
    @Override
    public String getCommandName() {
        return "warParty";
    }

    @Override
    public List<String> getAliases() {
        return List.of("wp");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::openWarPartyScreen);
    }

    private int openWarPartyScreen(CommandContext<CommandSourceStack> context) {
        try {
            Managers.TickScheduler.scheduleNextTick(
                    () -> Minecraft.getInstance().execute(() -> {
                        Models.War.mockWars();
                        Minecraft.getInstance().setScreen(new WarPartyScreen());
                    }));

        } catch (Exception exception) {
            SequoiaMod.error("Failed to open WarPartyScreen", exception);
            context.getSource()
                    .sendFailure(
                            SequoiaMod.prefix(Component.translatable("sequoia.command.warParty.failedToOpenScreen")));
        }

        return 1;
    }
}
