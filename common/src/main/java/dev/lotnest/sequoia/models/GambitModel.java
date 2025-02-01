package dev.lotnest.sequoia.models;

import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.utils.mc.LoreUtils;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Model;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;

public class GambitModel extends Model {
    private static HashMap<String, Boolean> gambits = new HashMap<String, Boolean>();;

    public GambitModel() {
        super(List.of());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onContainerSetContentPost(ContainerSetContentEvent.Post event) {
        if (!(McUtils.mc().screen instanceof ContainerScreen containerScreen) || !containerScreen.getTitle().getString().contains("\uDAFF\uDFE1\uE00C")) {
            return;
        }
        gambits.clear();
        for (ItemStack stack : containerScreen.getMenu().getItems()) {
            if (stack.getHoverName().getString().contains("Gambit")) {
                boolean enabled = false;
                for (StyledText line : LoreUtils.getLore(stack)) {
                    if (line.contains("Disable")) {
                        enabled = true;
                    }
                }
                gambits.put(stack.getHoverName().getString(), enabled);
            }
        }
    }

    public static boolean GetGambit(GambitType Gambit) {
     return gambits.getOrDefault(Gambit.displayName, false);
    }


    public enum GambitType {
        ANEMIC("Anemics Gambit"),
        ARCANE("Arcane Incontinent's Gambit"),
        BLEEDING("Bleeding Warrior's Gambit"),
        BURDENED("Burdened Pacifist's Gambit"),
        CURSED("Cursed Alchemist's Gambit"),
        DULL("Dull Blade's Gambit"),
        ERODED("Eroded Speedster's Gambit"),
        FARSIGHTED("Farsighted's Gambit"),
        FORESEEN("Foreseen Swordsman's Gambit"),
        GLUTTON("Glutton's Gambit"),
        HEMOPHILIAC("Hemophiliac's Gambit"),
        INGENUOUS("Ingenuous Mage's Gambit"),
        LEADEN("Leaden Fighter's Gambit"),
        MADDENING("Maddening Mage's Gambit"),
        MYOPIC("Myopic's Gambit"),
        OUTWORN("Outworn Soldier's Gambit"),
        SHATTERED("Shattered Mortal's Gambit");

        private final String displayName;

        private GambitType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
