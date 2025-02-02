/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.raids;

import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.utils.mc.PlayerUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TNARaidFeature extends Feature {
    private static final Pattern SHADOWLING_KILLED_PATTERN = Pattern.compile(
            ".*?A(?:§#........)?\\s+Shadowling(?:§#........)?\\s+has\\s+been\\s+killed!\\s+(?:§#........)?\\[(\\d+)/(\\d+)\\]");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (SequoiaMod.CONFIG.raidsFeature.TNARaidFeature.showShadowlingKilledTitle()) {
            Matcher shadowlingKilledMatcher = event.getOriginalStyledText().getMatcher(SHADOWLING_KILLED_PATTERN);
            if (shadowlingKilledMatcher.matches()) {
                SequoiaMod.debug("Shadowling killed: " + shadowlingKilledMatcher.group(1) + "/"
                        + shadowlingKilledMatcher.group(2));
                event.setCanceled(true);
                PlayerUtils.sendTitle(
                        Component.literal("Shadowling killed!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal(shadowlingKilledMatcher.group(1) + "/" + shadowlingKilledMatcher.group(2))
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }
}
