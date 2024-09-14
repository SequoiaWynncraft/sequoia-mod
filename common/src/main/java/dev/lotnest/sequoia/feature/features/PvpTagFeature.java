package dev.lotnest.sequoia.feature.features;

import com.wynntils.core.WynntilsMod;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.events.PvPEvent;
import dev.lotnest.sequoia.feature.Category;
import dev.lotnest.sequoia.feature.CategoryType;
import dev.lotnest.sequoia.feature.Feature;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

@Category(CategoryType.COMBAT)
public class PvpTagFeature extends Feature {
    private static final Pattern ENTERED_COMBAT_PATTERN =
            Pattern.compile("§cYou are in combat - do not logout or you will face a penalty.");

    private long lastTaggedMillis = 0L;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMessage(ChatMessageReceivedEvent event) {
        if (event.getStyledText().getMatcher(ENTERED_COMBAT_PATTERN).matches()) {
            WynntilsMod.postEvent(
                    new PvPEvent.Tagged(McUtils.player())); // FIXME: This has to get the TAGGER, not the tagged player
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPvPTagged(PvPEvent.Tagged event) {
        lastTaggedMillis = System.currentTimeMillis();

        McUtils.sendMessageToClient(Component.literal("TEST: Your armor:\n")
                .append(McUtils.player().getInventory().getArmor(0).getDisplayName())
                .append(Component.literal(" "))
                .append(McUtils.player().getInventory().getArmor(1).getDisplayName())
                .append(Component.literal(" "))
                .append(McUtils.player().getInventory().getArmor(2).getDisplayName())
                .append(Component.literal(" "))
                .append(McUtils.player().getInventory().getArmor(3).getDisplayName()));
    }
}
