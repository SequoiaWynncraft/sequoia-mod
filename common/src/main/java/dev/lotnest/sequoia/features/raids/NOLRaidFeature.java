/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.raids;

import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Feature;
import dev.lotnest.sequoia.utils.mc.PlayerUtils;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class NOLRaidFeature extends Feature {
    private static final Pattern LIGHT_ORB_FORMING = Pattern.compile("^A Light Orb is forming!$");
    private static final Pattern CRYSTALLINE_DECAYS_SPAWNED =
            Pattern.compile("^You have 30 seconds to kill all Crystalline Decays!$");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        String unfomattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        if (SequoiaMod.CONFIG.raidsFeature.NOLRaidFeature.showLightOrbFormingTitle()) {
            Matcher lightOrbFormingMatcher = LIGHT_ORB_FORMING.matcher(unfomattedMessage);
            if (lightOrbFormingMatcher.matches()) {
                event.setCanceled(true);
                PlayerUtils.sendTitle(
                        Component.literal("Light Orb forming!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            }
        }

        if (SequoiaMod.CONFIG.raidsFeature.NOLRaidFeature.showCrystallineDecaysSpawnedTitle()) {
            Matcher crystallineDecaysSpawnedMatcher = CRYSTALLINE_DECAYS_SPAWNED.matcher(unfomattedMessage);
            if (crystallineDecaysSpawnedMatcher.matches()) {
                event.setCanceled(true);
                PlayerUtils.sendTitle(Component.literal("Crystalline Decays spawned!")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }
}
