/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.core.components.Models;
import dev.lotnest.sequoia.core.consumers.command.Command;
import net.minecraft.commands.CommandSourceStack;

public class TerritoryMenuCommand extends Command {
    @Override
    public String getCommandName() {
        return "territoryMenu";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::openTerritoryMenu);
    }

    private int openTerritoryMenu(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        Models.Territory.openTerritoryMenu();
        return 1;
    }
}
