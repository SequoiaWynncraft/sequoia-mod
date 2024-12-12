package dev.lotnest.sequoia.feature.features.lootpool;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.utils.mc.LoreUtils;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.feature.Feature;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.regex.Pattern;

public class LootPoolFeature extends Feature {
    private static final Pattern SELECTED_CAMP_PATTERN = Pattern.compile("§6- §f.*");

    private static final int CHANGE_CAMP_ITEM_SLOT = 4;
    private static final int LOOT_POOL_CHEST_REWARD_STARTING_SLOT = 18;
    private static final int LOOT_POOL_CHEST_REWARD_ENDING_SLOT = 53;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onContainerSetContentPost(ContainerSetContentEvent.Post event) {
        if (!(McUtils.mc().screen instanceof ContainerScreen containerScreen)) {
            return;
        }

        List<StyledText> lore = LoreUtils.getLore(containerScreen.getMenu().getContainer().getItem(CHANGE_CAMP_ITEM_SLOT));
        if (lore.isEmpty()) {
            return;
        }

        StyledText selectedCampLoreLineText = StyledText.EMPTY;
        for (StyledText loreLine : lore) {
            if (loreLine.matches(SELECTED_CAMP_PATTERN)) {
                selectedCampLoreLineText = loreLine;
                break;
            }
        }
        if (selectedCampLoreLineText.isEmpty()) {
            return;
        }

        McUtils.sendMessageToClient(Component.literal(selectedCampLoreLineText.getStringWithoutFormatting().replace("-", "").trim()));

        for (int i = LOOT_POOL_CHEST_REWARD_STARTING_SLOT; i <= LOOT_POOL_CHEST_REWARD_ENDING_SLOT; i++) {
            ItemStack itemStack = containerScreen.getMenu().getContainer().getItem(i);
            if (itemStack == null || itemStack.isEmpty()) {
                continue;
            }
            Models.Item.getWynnItem(itemStack).ifPresent(wynnItem -> McUtils.sendMessageToClient(itemStack.getDisplayName()));
        }
    }

    @Override
    public boolean isExperimental() {
        return true;
    }
}
