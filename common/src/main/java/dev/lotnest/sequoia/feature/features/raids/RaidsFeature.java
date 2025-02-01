/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.feature.features.raids;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.mc.event.PlayerRenderEvent;
import com.wynntils.mc.extension.EntityRenderStateExtension;
import com.wynntils.utils.colors.CommonColors;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.utils.PlayerUtils;
import dev.lotnest.sequoia.wynn.WynnUtils;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

public class RaidsFeature extends Feature {
    private static final Pattern PLAYER_BUFF_CHOSEN =
            Pattern.compile("^(?<player>.+) chosen the (?<buff>.+) (?<buffTier>I|II|III) buff!$");

    private final Map<String, Set<Pair<String, String>>> raidBuffs = Maps.newHashMap();

    private static final MultiBufferSource.BufferSource BUFFER_SOURCE =
            MultiBufferSource.immediate(new ByteBufferBuilder(256));

    private static final int CIRCLE_SEGMENTS = 128;

    private static final float CIRCLE_HEIGHT = 0.2F;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        String unfomattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        if (SequoiaMod.CONFIG.raidsFeature.trackChosenPartyBuffs()) {
            Matcher playerBuffChosenMatcher = PLAYER_BUFF_CHOSEN.matcher(unfomattedMessage);
            if (playerBuffChosenMatcher.matches()) {
                String player = playerBuffChosenMatcher.group("player");
                String buff = playerBuffChosenMatcher.group("buff");
                String buffTier = playerBuffChosenMatcher.group("buffTier");

                raidBuffs.computeIfAbsent(player, k -> Sets.newHashSet()).add(Pair.of(buff, buffTier));

                SequoiaMod.debug("raidsBuffs: " + raidBuffs);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRender(PlayerRenderEvent event) {
        if (!isEnabled()) {
            return;
        }
        Entity entity = ((EntityRenderStateExtension) event.getPlayerRenderState()).getEntity();
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }

        if (!Models.WorldState.onWorld() && !Models.WorldState.onHousing()) {
            return;
        }

        if (!PlayerUtils.isSelf(player)) {
            return;
        }

        if (SequoiaMod.CONFIG.raidsFeature.farsightedOverlay()) {
            WynnUtils.renderCircle(
                    BUFFER_SOURCE,
                    CIRCLE_SEGMENTS,
                    CIRCLE_HEIGHT,
                    event.getPoseStack(),
                    player.position(),
                    3.0F,
                    CommonColors.LIGHT_BLUE.withAlpha((95)).asInt());
        }
        if (SequoiaMod.CONFIG.raidsFeature.myopicOverlay()) {
            WynnUtils.renderCircle(
                    BUFFER_SOURCE,
                    CIRCLE_SEGMENTS,
                    CIRCLE_HEIGHT,
                    event.getPoseStack(),
                    player.position(),
                    12.0F,
                    CommonColors.LIGHT_GREEN.withAlpha((95)).asInt());
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }
}
