/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.raids;

import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.mc.event.BossHealthUpdateEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.utils.mc.PlayerUtils;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class NOLRaidFeature extends Feature {
    private static final Pattern LIGHT_ORB_FORMING = Pattern.compile("^A Light Orb is forming!$");
    private static final Pattern CRYSTALLINE_DECAYS_SPAWNED =
            Pattern.compile("^You have 30 seconds to kill all Crystalline Decays!$");
    private static boolean Skipped = false;
    private final Pattern SkipPattern = Pattern.compile("Press . SWAP HANDS to skip - [0-9]+/4");
    private final Pattern GuildPattern = Pattern.compile("§7Lv\\. [0-9]+§f - §l§[A-Za-z]+§f - §7[0-9]+% XP");

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onNolCutsceneCheck(BossHealthUpdateEvent event) {
        if (!SequoiaMod.CONFIG.raidsFeature.NOLRaidFeature.autoSkipCutscene()) {
            return;
        }
        event.getBossEvents().forEach((uuid, lerpingBossEvent) -> {
            String text = lerpingBossEvent.getName().getString();
            if (SkipPattern.matcher(text).matches()) {
                if (!Skipped) {
                    McUtils.sendMessageToClient(
                            SequoiaMod.prefix(Component.literal("§eAttempting to skip cutscene...")));
                    KeyMapping.click(Minecraft.getInstance().options.keySwapOffhand.key);
                    Skipped = true;
                }

            } else if (GuildPattern.matcher(text).matches())
                ;
            else {
                Skipped = false;
            }
        });
    }

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
