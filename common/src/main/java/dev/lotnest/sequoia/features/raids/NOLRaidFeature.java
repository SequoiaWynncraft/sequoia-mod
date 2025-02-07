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
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class NOLRaidFeature extends Feature {
    private static final Pattern LIGHT_ORB_FORMING_PATTERN = Pattern.compile("^A Light Orb is forming!$");
    private static final Pattern CRYSTALLINE_DECAYS_SPAWNED_PATTERN =
            Pattern.compile("^You have 30 seconds to kill all Crystalline Decays!$");
    private static final Pattern CUTSCENE_SKIP_BOSS_BAR_PATTERN =
            Pattern.compile("Press . SWAP HANDS to skip - \\d+/4");
    private static final Pattern GUILD_BOSS_BAR_PATTERN =
            Pattern.compile("§7Lv\\. \\d+§f - §l§[A-Za-z]+§f - §7\\d+% XP");

    private boolean isCutsceneSkipped = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onNolCutsceneCheck(BossHealthUpdateEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (!SequoiaMod.CONFIG.raidsFeature.NOLRaidFeature.autoSkipCutscene()) {
            return;
        }

        event.getBossEvents().forEach((uuid, lerpingBossEvent) -> {
            String text = lerpingBossEvent.getName().getString();
            if (CUTSCENE_SKIP_BOSS_BAR_PATTERN.matcher(text).matches()) {
                if (!isCutsceneSkipped) {
                    McUtils.sendMessageToClient(
                            SequoiaMod.prefix(Component.translatable("sequoia.feature.nOLRaid.skippingCutscene")));
                    PlayerUtils.swapOffhand();
                    isCutsceneSkipped = true;
                }
            } else if (!GUILD_BOSS_BAR_PATTERN.matcher(text).matches()) {
                isCutsceneSkipped = false;
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        String unformattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        if (SequoiaMod.CONFIG.raidsFeature.NOLRaidFeature.showLightOrbFormingTitle()) {
            Matcher lightOrbFormingMatcher = LIGHT_ORB_FORMING_PATTERN.matcher(unformattedMessage);
            if (lightOrbFormingMatcher.matches()) {
                event.setCanceled(true);
                PlayerUtils.sendTitle(Component.translatable("sequoia.feature.nOLRaid.lightOrbFormingTitle"));
            }
        }

        if (SequoiaMod.CONFIG.raidsFeature.NOLRaidFeature.showCrystallineDecaysSpawnedTitle()) {
            Matcher crystallineDecaysSpawnedMatcher = CRYSTALLINE_DECAYS_SPAWNED_PATTERN.matcher(unformattedMessage);
            if (crystallineDecaysSpawnedMatcher.matches()) {
                event.setCanceled(true);
                PlayerUtils.sendTitle(Component.translatable("sequoia.feature.nOLRaid.crystallineDecaysSpawnedTitle"));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isCutsceneSkipped = false;
    }
}
