package dev.lotnest.sequoia.feature.features;

import com.wynntils.models.character.event.CharacterDeathEvent;
import dev.lotnest.sequoia.feature.Category;
import dev.lotnest.sequoia.feature.CategoryType;
import dev.lotnest.sequoia.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

@Category(CategoryType.SOUNDS)
public class CustomDeathSoundFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCharacterDeath(CharacterDeathEvent ignored) {
        Minecraft.getInstance()
                .getSoundManager()
                .play(SimpleSoundInstance.forUI(
                        SoundEvent.createVariableRangeEvent(ResourceLocation.parse("minecraft:entity.ghast.scream")),
                        1.0F));
    }
}
