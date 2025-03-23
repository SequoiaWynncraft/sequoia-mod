/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features;

import com.google.common.collect.Lists;
import com.wynntils.mc.event.TickAlwaysEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class SequoiaOSTFeature extends Feature {
    private static final ResourceLocation OST_SEQUOIA_THEME_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.sequoia-theme");
    private static final SoundEvent OST_SEQUOIA_THEME = SoundEvent.createVariableRangeEvent(OST_SEQUOIA_THEME_ID);
    private static final SoundInstance OST_SEQUOIA_THEME_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_SEQUOIA_THEME.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_THE_BEAST_OF_LIGHT_FOREST_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.the-beast-of-light-forest");
    private static final SoundEvent OST_THE_BEAST_OF_LIGHT_FOREST =
            SoundEvent.createVariableRangeEvent(OST_THE_BEAST_OF_LIGHT_FOREST_ID);
    private static final SoundInstance OST_THE_BEAST_OF_LIGHT_FOREST_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_THE_BEAST_OF_LIGHT_FOREST.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_CORROSIVE_PAINT_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.corrosive-paint");
    private static final SoundEvent OST_CORROSIVE_PAINT = SoundEvent.createVariableRangeEvent(OST_CORROSIVE_PAINT_ID);
    private static final SoundInstance OST_CORROSIVE_PAINT_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_CORROSIVE_PAINT.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_TAKING_ITS_TOLL_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.taking-its-tol");
    private static final SoundEvent OST_TAKING_ITS_TOLL = SoundEvent.createVariableRangeEvent(OST_TAKING_ITS_TOLL_ID);
    private static final SoundInstance OST_TAKING_ITS_TOLL_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_TAKING_ITS_TOLL.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_TOWER_REMOVAL_SERVICE_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.tower-removal-service");
    private static final SoundEvent OST_TOWER_REMOVAL_SERVICE =
            SoundEvent.createVariableRangeEvent(OST_TOWER_REMOVAL_SERVICE_ID);
    private static final SoundInstance OST_TOWER_REMOVAL_SERVICE_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_TOWER_REMOVAL_SERVICE.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_SEMPER_VIRENS_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.semper-virens");
    private static final SoundEvent OST_SEMPER_VIRENS = SoundEvent.createVariableRangeEvent(OST_SEMPER_VIRENS_ID);
    private static final SoundInstance OST_SEMPER_VIRENS_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_SEMPER_VIRENS.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_ROOTED_IN_STONE_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.rooted-in-stone");
    private static final SoundEvent OST_ROOTED_IN_STONE = SoundEvent.createVariableRangeEvent(OST_ROOTED_IN_STONE_ID);
    private static final SoundInstance OST_ROOTED_IN_STONE_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_ROOTED_IN_STONE.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_CONTAINMENT_PROTOCOL_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.containment-protocol");
    private static final SoundEvent OST_CONTAINMENT_PROTOCOL =
            SoundEvent.createVariableRangeEvent(OST_CONTAINMENT_PROTOCOL_ID);
    private static final SoundInstance OST_CONTAINMENT_PROTOCOL_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_CONTAINMENT_PROTOCOL.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_MONSTER_UNDER_THE_BED_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.monster-under-the-bed");
    private static final SoundEvent OST_MONSTER_UNDER_THE_BED =
            SoundEvent.createVariableRangeEvent(OST_MONSTER_UNDER_THE_BED_ID);
    private static final SoundInstance OST_MONSTER_UNDER_THE_BED_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_MONSTER_UNDER_THE_BED.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_SHUT_UP_MOM_IM_GAMING_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.shut-up-mom-im-gaming");
    private static final SoundEvent OST_SHUT_UP_MOM_IM_GAMING =
            SoundEvent.createVariableRangeEvent(OST_SHUT_UP_MOM_IM_GAMING_ID);
    private static final SoundInstance OST_SHUT_UP_MOM_IM_GAMING_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_SHUT_UP_MOM_IM_GAMING.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_EVERYTHING_IN_ITS_PLACE_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.everything-in-its-place");
    private static final SoundEvent OST_EVERYTHING_IN_ITS_PLACE =
            SoundEvent.createVariableRangeEvent(OST_EVERYTHING_IN_ITS_PLACE_ID);
    private static final SoundInstance OST_EVERYTHING_IN_ITS_PLACE_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_EVERYTHING_IN_ITS_PLACE.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_WELL_MAID_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.well-maid");
    private static final SoundEvent OST_WELL_MAID = SoundEvent.createVariableRangeEvent(OST_WELL_MAID_ID);
    private static final SoundInstance OST_WELL_MAID_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_WELL_MAID.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_CALL_OF_THE_VOID_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.call-of-the-void");
    private static final SoundEvent OST_CALL_OF_THE_VOID = SoundEvent.createVariableRangeEvent(OST_CALL_OF_THE_VOID_ID);
    private static final SoundInstance OST_CALL_OF_THE_VOID_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_CALL_OF_THE_VOID.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_MOONRISE_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.moonrise");
    private static final SoundEvent OST_MOONRISE = SoundEvent.createVariableRangeEvent(OST_MOONRISE_ID);
    private static final SoundInstance OST_MOONRISE_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_MOONRISE.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_HYMN_OF_HATE_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.hymn-of-hate");
    private static final SoundEvent OST_HYMN_OF_HATE = SoundEvent.createVariableRangeEvent(OST_HYMN_OF_HATE_ID);
    private static final SoundInstance OST_HYMN_OF_HATE_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_HYMN_OF_HATE.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_WRATH_OF_THE_OCEAN_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.wrath-of-the-ocean");
    private static final SoundEvent OST_WRATH_OF_THE_OCEAN =
            SoundEvent.createVariableRangeEvent(OST_WRATH_OF_THE_OCEAN_ID);
    private static final SoundInstance OST_WRATH_OF_THE_OCEAN_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_WRATH_OF_THE_OCEAN.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_AURA_BUFFET_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.aura-buffet");
    private static final SoundEvent OST_AURA_BUFFET = SoundEvent.createVariableRangeEvent(OST_AURA_BUFFET_ID);
    private static final SoundInstance OST_AURA_BUFFET_SOUND_INSTANCE = new SimpleSoundInstance(
            OST_AURA_BUFFET.location(),
            SoundSource.AMBIENT,
            1,
            1,
            SoundInstance.createUnseededRandom(),
            false,
            0,
            SoundInstance.Attenuation.NONE,
            0.0,
            0.0,
            0.0,
            true);

    private static final ResourceLocation OST_FLYING_KICK_ME_INTO_A_WALL_PRETTY_PLEASE_ID =
            ResourceLocation.fromNamespaceAndPath(SequoiaMod.MOD_ID, "ost.flying-kick-me-into-a-wall-pretty-please");
    private static final SoundEvent OST_FLYING_KICK_ME_INTO_A_WALL_PRETTY_PLEASE =
            SoundEvent.createVariableRangeEvent(OST_FLYING_KICK_ME_INTO_A_WALL_PRETTY_PLEASE_ID);
    private static final SoundInstance OST_FLYING_KICK_ME_INTO_A_WALL_PRETTY_PLEASE_SOUND_INSTANCE =
            new SimpleSoundInstance(
                    OST_FLYING_KICK_ME_INTO_A_WALL_PRETTY_PLEASE.location(),
                    SoundSource.AMBIENT,
                    1,
                    1,
                    SoundInstance.createUnseededRandom(),
                    false,
                    0,
                    SoundInstance.Attenuation.NONE,
                    0.0,
                    0.0,
                    0.0,
                    true);

    private final List<SoundInstance> playlist;
    private int currentIndex;
    private SoundInstance currentTrack;

    public SequoiaOSTFeature() {
        playlist = Lists.newArrayList(
                OST_SEQUOIA_THEME_SOUND_INSTANCE,
                OST_THE_BEAST_OF_LIGHT_FOREST_SOUND_INSTANCE,
                OST_CORROSIVE_PAINT_SOUND_INSTANCE,
                OST_TAKING_ITS_TOLL_SOUND_INSTANCE,
                OST_TOWER_REMOVAL_SERVICE_SOUND_INSTANCE,
                OST_SEMPER_VIRENS_SOUND_INSTANCE,
                OST_ROOTED_IN_STONE_SOUND_INSTANCE,
                OST_CONTAINMENT_PROTOCOL_SOUND_INSTANCE,
                OST_MONSTER_UNDER_THE_BED_SOUND_INSTANCE,
                OST_SHUT_UP_MOM_IM_GAMING_SOUND_INSTANCE,
                OST_EVERYTHING_IN_ITS_PLACE_SOUND_INSTANCE,
                OST_WELL_MAID_SOUND_INSTANCE,
                OST_CALL_OF_THE_VOID_SOUND_INSTANCE,
                OST_MOONRISE_SOUND_INSTANCE,
                OST_HYMN_OF_HATE_SOUND_INSTANCE,
                OST_WRATH_OF_THE_OCEAN_SOUND_INSTANCE,
                OST_AURA_BUFFET_SOUND_INSTANCE,
                OST_FLYING_KICK_ME_INTO_A_WALL_PRETTY_PLEASE_SOUND_INSTANCE);

        shufflePlaylist();
    }

    private void shufflePlaylist() {
        Collections.shuffle(playlist, new Random());
        currentIndex = 0;
    }

    public SoundInstance getNextTrack() {
        if (currentIndex >= playlist.size()) {
            shufflePlaylist();
        }
        return playlist.get(currentIndex++);
    }

    public boolean isPlaying() {
        return currentTrack != null && Minecraft.getInstance().getSoundManager().isActive(currentTrack);
    }

    public void playNextTrack() {
        stopCurrentTrack();

        if (isEnabled()) {
            SoundInstance nextTrack = getNextTrack();
            currentTrack = nextTrack;

            Minecraft.getInstance().getSoundManager().play(nextTrack);
        }
    }

    public void stopCurrentTrack() {
        if (isPlaying()) {
            Minecraft.getInstance().getSoundManager().stop(currentTrack);
            currentTrack = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.sequoiaOSTFeature.enabled();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        shufflePlaylist();
        playNextTrack();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        playlist.forEach(
                soundInstance -> Minecraft.getInstance().getSoundManager().stop(soundInstance));
        shufflePlaylist();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTickAlways(TickAlwaysEvent ignored) {
        if (isEnabled() && !isPlaying()) {
            playNextTrack();
        }
    }
}
