/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features;

import com.wynntils.mc.event.CommandSentEvent;
import dev.lotnest.sequoia.core.components.Feature;
import dev.lotnest.sequoia.core.components.Managers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class CommandsFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommand(CommandSentEvent event) {
        String command = event.getCommand();
        if (Managers.Command.handleCommand(command)) {
            event.setCanceled(true);
        }
    }
}
