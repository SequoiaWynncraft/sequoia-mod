/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.raids;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.wynntils.mc.event.PlayerRenderEvent;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.mc.extension.EntityRenderStateExtension;
import com.wynntils.models.character.event.CharacterUpdateEvent;
import com.wynntils.models.raid.event.RaidEndedEvent;
import com.wynntils.models.raid.type.RaidRoomType;
import com.wynntils.models.spells.event.SpellEvent;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Models;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.models.GambitModel;
import dev.lotnest.sequoia.utils.mc.PlayerUtils;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class RaidsFeature extends Feature {
    private static final MultiBufferSource.BufferSource BUFFER_SOURCE =
            MultiBufferSource.immediate(new ByteBufferBuilder(256));

    private static final int CIRCLE_SEGMENTS = 128;

    private static final float CIRCLE_HEIGHT = 0.2F;

    private boolean isGluttonWarningDisplayed = false;

    private int spellsCasted = 0;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGluttonGambitCheck(TickEvent event) {
        if (SequoiaMod.CONFIG.raidsFeature.gluttonyDisplayType() == GluttonyWarningType.DISABLED) {
            return;
        }

        if (com.wynntils.core.components.Models.Raid.getCurrentRoom() != RaidRoomType.BUFF_3
                || !Models.Gambit.hasChosenGambit(GambitModel.GambitType.GLUTTON)) {
            isGluttonWarningDisplayed = false;
            return;
        }

        if (!isGluttonWarningDisplayed
                && com.wynntils.core.components.Models.Raid.getCurrentRoom() == RaidRoomType.BUFF_3
                && Models.Raid.getRaidBuffs(McUtils.playerName()).size() == 2) {
            switch (SequoiaMod.CONFIG.raidsFeature.gluttonyDisplayType()) {
                case TEXT -> McUtils.sendMessageToClient(SequoiaMod.prefix(
                        Component.translatable("sequoia.feature.raidsFeature.gluttonGambitWarningText")));
                case TITLE -> PlayerUtils.sendTitle(
                        Component.translatable("sequoia.feature.raidsFeature.gluttonGambitWarningTitle"));
                default -> throw new IllegalStateException(
                        "Unexpected value: " + SequoiaMod.CONFIG.raidsFeature.gluttonyDisplayType());
            }
        }
        isGluttonWarningDisplayed = true;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRender(PlayerRenderEvent event) {
        if (!isEnabled()) return;

        Entity entity = ((EntityRenderStateExtension) event.getPlayerRenderState()).getEntity();
        if (!(entity instanceof AbstractClientPlayer player)) return;

        if (!com.wynntils.core.components.Models.WorldState.onWorld()
                || !com.wynntils.core.components.Models.WorldState.onHousing()) return;
        if (!PlayerUtils.isSelf(player)) return;

        switch (SequoiaMod.CONFIG.raidsFeature.farsightedGambitOverlayDisplayType()) {
            case AUTOMATIC -> {
                if (Models.Gambit.hasChosenGambit(GambitModel.GambitType.FARSIGHTED)
                        && com.wynntils.core.components.Models.Raid.getCurrentRaid() != null) {
                    WynnUtils.renderCircle(
                            BUFFER_SOURCE,
                            CIRCLE_SEGMENTS,
                            CIRCLE_HEIGHT,
                            event.getPoseStack(),
                            player.position(),
                            3.0F,
                            CommonColors.LIGHT_BLUE.withAlpha((95)).asInt());
                }
            }
            case FORCED -> WynnUtils.renderCircle(
                    BUFFER_SOURCE,
                    CIRCLE_SEGMENTS,
                    CIRCLE_HEIGHT,
                    event.getPoseStack(),
                    player.position(),
                    3.0F,
                    CommonColors.LIGHT_BLUE.withAlpha((95)).asInt());

            case DISABLED -> {}
            default -> throw new IllegalStateException(
                    "Unexpected value: " + SequoiaMod.CONFIG.raidsFeature.farsightedGambitOverlayDisplayType());
        }

        switch (SequoiaMod.CONFIG.raidsFeature.myopicGambitOverlayDisplayType()) {
            case AUTOMATIC -> {
                if (Models.Gambit.hasChosenGambit(GambitModel.GambitType.MYOPIC)
                        && com.wynntils.core.components.Models.Raid.getCurrentRaid() != null) {
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
            case FORCED -> WynnUtils.renderCircle(
                    BUFFER_SOURCE,
                    CIRCLE_SEGMENTS,
                    CIRCLE_HEIGHT,
                    event.getPoseStack(),
                    player.position(),
                    12.0F,
                    CommonColors.LIGHT_GREEN.withAlpha((95)).asInt());
            case DISABLED -> {}
            default -> throw new IllegalStateException(
                    "Unexpected value: " + SequoiaMod.CONFIG.raidsFeature.myopicGambitOverlayDisplayType());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCharacterUpdate(CharacterUpdateEvent event) {
        if (isGluttonWarningDisplayed) {
            isGluttonWarningDisplayed = false;
        }

        spellsCasted = 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldStateUpdate(WorldStateEvent event) {
        if (isGluttonWarningDisplayed) {
            isGluttonWarningDisplayed = false;
        }

        spellsCasted = 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidCompletedEvent(RaidEndedEvent.Completed event) {
        if (isGluttonWarningDisplayed) {
            isGluttonWarningDisplayed = false;
        }

        spellsCasted = 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRaidFailedEvent(RaidEndedEvent.Failed event) {
        if (isGluttonWarningDisplayed) {
            isGluttonWarningDisplayed = false;
        }

        spellsCasted = 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onSpellCast(SpellEvent.Cast event) {
        if (!SequoiaMod.CONFIG.raidsFeature.maddeningMageGambitMiscastTracker()) return;
        if (!Models.Gambit.hasChosenGambit(GambitModel.GambitType.MADDENING)) return;
        if (com.wynntils.core.components.Models.Raid.getCurrentRaid() == null) return;

        // We want to track the spells cast by the player, regardless of the feature state, as the player may
        // enable the feature at later time.
        spellsCasted++;
        if (spellsCasted == 9) {
            spellsCasted = 0;

            if (!isEnabled()) return;

            PlayerUtils.sendTitle(
                    Component.translatable("sequoia.feature.raidsFeature.maddeningMageGambitMiscastWarningTitle"),
                    Component.translatable("sequoia.feature.raidsFeature.maddeningMageGambitMiscastWarningSubtitle"));
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.raidsFeature.enabled();
    }

    public enum RangeIndicatorDisplayType {
        AUTOMATIC,
        FORCED,
        DISABLED
    }

    public enum GluttonyWarningType {
        TEXT,
        TITLE,
        DISABLED
    }
}
