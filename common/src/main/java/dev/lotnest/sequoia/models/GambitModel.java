/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models;

import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.utils.mc.LoreUtils;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.core.components.Model;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class GambitModel extends Model {
    private final HashMap<String, Boolean> chosenGambits = new HashMap<>();

    public GambitModel() {
        super(List.of());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onContainerSetContentPost(ContainerSetContentEvent.Post event) {
        if (!(McUtils.mc().screen instanceof ContainerScreen containerScreen)
                || !containerScreen.getTitle().getString().contains("\uDAFF\uDFE1\uE00C")) {
            return;
        }

        chosenGambits.clear();

        for (ItemStack stack : containerScreen.getMenu().getItems()) {
            if (stack.getHoverName().getString().contains("Gambit")) {
                boolean isGambitEnabled = false;

                for (StyledText line : LoreUtils.getLore(stack)) {
                    if (line.contains("Disable")) {
                        isGambitEnabled = true;
                    }
                }

                chosenGambits.put(stack.getHoverName().getString(), isGambitEnabled);
            }
        }
    }

    public boolean getGambit(GambitType gambitType) {
        return chosenGambits.getOrDefault(gambitType.displayName, false);
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
