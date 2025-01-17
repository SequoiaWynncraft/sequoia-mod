/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.feature.features;

import com.google.common.collect.Lists;
import com.wynntils.mc.event.TickAlwaysEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.sound.SequoiaSounds;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class SequoiaOSTFeature extends Feature {
    private final List<SoundInstance> playlist;
    private int currentIndex;
    private SoundInstance currentTrack;

    public SequoiaOSTFeature() {
        playlist = Lists.newArrayList(
                SequoiaSounds.OST_SEQUOIA_THEME_SOUND_INSTANCE,
                SequoiaSounds.OST_THE_BEAST_OF_LIGHT_FOREST_SOUND_INSTANCE,
                SequoiaSounds.OST_CORROSIVE_PAINT_SOUND_INSTANCE,
                SequoiaSounds.OST_TAKING_ITS_TOLL_SOUND_INSTANCE,
                SequoiaSounds.OST_TOWER_REMOVAL_SERVICE_SOUND_INSTANCE,
                SequoiaSounds.OST_SEMPER_VIRENS_SOUND_INSTANCE,
                SequoiaSounds.OST_ROOTED_IN_STONE_SOUND_INSTANCE,
                SequoiaSounds.OST_CONTAINMENT_PROTOCOL_SOUND_INSTANCE,
                SequoiaSounds.OST_MONSTER_UNDER_THE_BED_SOUND_INSTANCE,
                SequoiaSounds.OST_SHUT_UP_MOM_IM_GAMING_SOUND_INSTANCE,
                SequoiaSounds.OST_EVERYTHING_IN_ITS_PLACE_SOUND_INSTANCE,
                SequoiaSounds.OST_WELL_MAID_SOUND_INSTANCE,
                SequoiaSounds.OST_CALL_OF_THE_VOID_SOUND_INSTANCE,
                SequoiaSounds.OST_MOONRISE_SOUND_INSTANCE,
                SequoiaSounds.OST_HYMN_OF_HATE_SOUND_INSTANCE,
                SequoiaSounds.OST_WRATH_OF_THE_OCEAN_SOUND_INSTANCE,
                SequoiaSounds.OST_AURA_BUFFET_SOUND_INSTANCE,
                SequoiaSounds.OST_FLYING_KICK_ME_INTO_A_WALL_PRETTY_PLEASE_SOUND_INSTANCE);

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
