package dev.lotnest.sequoia.feature.features;

import com.wynntils.mc.event.CommandSentEvent;
import dev.lotnest.sequoia.feature.Category;
import dev.lotnest.sequoia.feature.CategoryType;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.manager.Managers;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Category(CategoryType.SEQUOIA)
public class CommandsFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommand(CommandSentEvent event) {
        String command = event.getCommand();
        if (Managers.Command.handleCommand(command)) {
            event.setCanceled(true);
        }
    }
}
