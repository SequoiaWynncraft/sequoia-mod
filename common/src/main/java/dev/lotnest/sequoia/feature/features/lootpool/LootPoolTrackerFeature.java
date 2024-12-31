package dev.lotnest.sequoia.feature.features.lootpool;

import dev.lotnest.sequoia.feature.Feature;
import java.util.regex.Pattern;

public class LootPoolTrackerFeature extends Feature {
    private static final Pattern SELECTED_CAMP_PATTERN = Pattern.compile("§6- §f.*");

    private static final int CHANGE_CAMP_ITEM_SLOT = 4;
    private static final int LOOT_POOL_CHEST_REWARD_STARTING_SLOT = 18;
    private static final int LOOT_POOL_CHEST_REWARD_ENDING_SLOT = 53;

    //    @SubscribeEvent(priority = EventPriority.HIGHEST)
    //    public void onContainerSetContentPost(ContainerSetContentEvent.Post event) {
    //        if (!SequoiaMod.CONFIG.lootPoolTrackerFeature.enabled()) {
    //            return;
    //        }
    //
    //        if (!(McUtils.mc().screen instanceof ContainerScreen containerScreen)) {
    //            return;
    //        }
    //
    //        List<StyledText> lore =
    //                LoreUtils.getLore(containerScreen.getMenu().getContainer().getItem(CHANGE_CAMP_ITEM_SLOT));
    //        if (lore.isEmpty()) {
    //            return;
    //        }
    //
    //        StyledText selectedCampLoreLineText = StyledText.EMPTY;
    //        for (StyledText loreLine : lore) {
    //            if (loreLine.matches(SELECTED_CAMP_PATTERN)) {
    //                selectedCampLoreLineText = loreLine;
    //                break;
    //            }
    //        }
    //        if (selectedCampLoreLineText.isEmpty()) {
    //            return;
    //        }
    //
    //        String selectedCampName = selectedCampLoreLineText
    //                .getStringWithoutFormatting()
    //                .replace("-", "")
    //                .trim();
    //        List<LootPoolItem> lootPoolItems = Lists.newArrayList();
    //
    //        for (int i = LOOT_POOL_CHEST_REWARD_STARTING_SLOT; i <= LOOT_POOL_CHEST_REWARD_ENDING_SLOT; i++) {
    //            ItemStack itemStack = containerScreen.getMenu().getContainer().getItem(i);
    //            if (itemStack == null || itemStack.isEmpty()) {
    //                continue;
    //            }
    //
    //            Models.Item.getWynnItem(itemStack).ifPresent(wynnItem -> lootPoolItems.add(new
    // LootPoolItem(wynnItem)));
    //        }
    //
    //        SequoiaMod.debug("[LootPoolTrackerFeature] "
    //                + new GLootPoolWSMessage(new GLootPoolWSMessage.Data(selectedCampName, lootPoolItems)));
    //    }

    @Override
    public boolean isExperimental() {
        return true;
    }
}
