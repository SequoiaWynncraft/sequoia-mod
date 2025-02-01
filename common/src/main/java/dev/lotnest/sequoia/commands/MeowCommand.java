/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.core.consumers.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.sounds.SoundEvents;

public class MeowCommand extends Command {
    @Override
    public String getCommandName() {
        return "meow";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.executes(this::meowInGuildChat);
    }

    private int meowInGuildChat(CommandContext<CommandSourceStack> context) {
        McUtils.sendChat("/g meow");
        McUtils.playSoundUI(SoundEvents.CAT_PURREOW);
        return 1;
    }
}
