package dev.lotnest.sequoia.feature.features;

import com.google.common.collect.Maps;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.wynn.WynnUtils;
import java.util.Map;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class OuterVoidTrackerFeature extends Feature {
    // Thanks to PyroKlee and davie123dx for providing some item names!
    private static final Set<String> ITEM_NAMES = Set.of(
            "Fallen Blade",
            "Shriveled Voidgloom",
            "Due Delivery",
            "Wind-shorn Stone",
            "Discarded Scrap",
            "Elestial Voidstone",
            "Precious Mineral",
            "Tangible Intangibility",
            "Lone Component",
            "Metal Plate",
            "Large Metal Chunk",
            "Luxury Timepiece",
            "Golem Capacitor",
            "Void Carapace",
            "Black Prism",
            "Piezoelectric Voidquartz",
            "Anti-proportional circuit",
            "Almanac Page",
            "Matte Bead",
            "Voidwarped Root",
            "History Textbook",
            "Hobby Horse",
            "Pachaged Brownie",
            "Relic of the Shattering",
            "Small Ruby",
            "Bleached Branch",
            "Missing Coinage",
            "Amphora Sherd",
            "Scrap Chain",
            "Miracle Leftovers",
            "Bottle of Yogurt",
            "Metal Swarf",
            "Arable Chunk",
            "Fossilized Starfish",
            "Void Slime",
            "Fallen Sand",
            "Mosaic Tile",
            "Frying Pan",
            "Lightless Blossom",
            "Steel Rod",
            "Abandoned Pot",
            "Provisional Piton",
            "Improvised Chainshot");
    private final Map<String, Integer> neededItems = Maps.newConcurrentMap();

    public Set<String> getItemNames() {
        return ITEM_NAMES;
    }

    public boolean itemNameExists(String itemName) {
        for (String name : ITEM_NAMES) {
            if (name.equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    public void addNeededItem(String itemName, int itemCount) {
        if (hasNeededItem(itemName)) {
            itemCount += getNeededItemCount(itemName);
        }
        neededItems.put(itemName, itemCount);
    }

    public void removeNeededItem(String itemName) {
        neededItems.remove(itemName);
    }

    public boolean hasNeededItem(String itemName) {
        for (String name : neededItems.keySet()) {
            if (name.equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    public int getNeededItemCount(String itemName) {
        return neededItems.get(itemName);
    }

    public void clearNeededItems() {
        neededItems.clear();
    }

    public Set<String> getNeededItems() {
        return neededItems.keySet();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (!Models.WorldState.onWorld()) {
            return;
        }

        if (neededItems.isEmpty()) {
            return;
        }

        McUtils.inventory().items.forEach(itemStack -> {
            String itemName =
                    WynnUtils.getUnformattedString(itemStack.getDisplayName().getString());
            if (StringUtils.isBlank(itemName) || itemStack.getCount() <= 0) {
                return;
            }

            if (itemNameExists(itemName) && hasNeededItem(itemName)) {
                int itemCountNeeded = getNeededItemCount(itemName);
                if (itemStack.getCount() >= itemCountNeeded) {
                    neededItems.remove(itemName);

                    if (SequoiaMod.CONFIG.outerVoidTrackerFeature.playSoundEffect()) {
                        McUtils.playSoundUI(SoundEvents.PLAYER_LEVELUP);
                    }

                    McUtils.sendMessageToClient(SequoiaMod.prefix(
                            Component.translatable("sequoia.feature.outerVoidTracker.enoughNeededItemCollected")
                                    .withStyle(ChatFormatting.GREEN)
                                    .append(itemStack.getHoverName())
                                    .append(Component.literal("!").withStyle(ChatFormatting.GREEN))));
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.outerVoidTrackerFeature.enabled();
    }
}
