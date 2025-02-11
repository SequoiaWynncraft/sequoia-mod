/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.core.components.Models;
import dev.lotnest.sequoia.core.consumers.command.Command;
import dev.lotnest.sequoia.mc.screens.WarPartyScreen;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class PartyCommand extends Command {
    @Override
    public String getCommandName() {
        return "warparties";
    }

    @Override
    public List<String> getAliases() {
        return List.of("wp");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::tryOpenWarPartyGUI);
    }

    private int tryOpenWarPartyGUI(CommandContext<CommandSourceStack> context) {
        try {
            System.out.println("[WarParties] Attempting to open WarPartyScreen...");

            Executors.newSingleThreadScheduledExecutor()
                    .schedule(
                            () -> Minecraft.getInstance().execute(() -> {
                                Models.War.mockWars();
                                Minecraft.getInstance()
                                        .setScreen(new WarPartyScreen(
                                                Models.WarParty.getActiveWarParties(), Models.War.getActiveWars()));
                            }),
                            1,
                            TimeUnit.MILLISECONDS);

        } catch (Exception exception) {
            System.err.println("[WarParties] Failed to open WarPartyScreen!");
            exception.printStackTrace();
            context.getSource().sendFailure(Component.literal("Error opening WarPartyScreen!"));
        }

        return 1;
    }
}
