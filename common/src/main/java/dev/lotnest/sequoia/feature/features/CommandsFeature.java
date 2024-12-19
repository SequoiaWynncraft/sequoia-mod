package dev.lotnest.sequoia.feature.features;

import com.wynntils.mc.event.CommandSentEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.manager.Managers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class CommandsFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommand(CommandSentEvent event) {
        if (SequoiaMod.CONFIG.commandsFeature.enabled()) {
            String command = event.getCommand();
            if (Managers.Command.handleCommand(command)) {
                event.setCanceled(true);
            }
        }
    }
}
