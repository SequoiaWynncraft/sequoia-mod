package dev.lotnest.sequoia.feature.features;

import com.wynntils.core.components.Managers;
import com.wynntils.mc.event.AddEntityEvent;
import com.wynntils.mc.event.ChangeCarriedItemEvent;
import com.wynntils.mc.event.ConnectionEvent;
import com.wynntils.mc.event.RemoveEntitiesEvent;
import com.wynntils.models.character.event.CharacterUpdateEvent;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.feature.Category;
import dev.lotnest.sequoia.feature.CategoryType;
import dev.lotnest.sequoia.feature.Feature;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;

@Category(CategoryType.COMBAT)
public class MantleOfTheBovemistsTrackerFeature extends Feature {
    private static final double MANTLE_OF_THE_BOVEMISTS_SEARCH_RADIUS = 4.5;

    private List<Integer> spawnedMantleOfTheBovemistsIds = Lists.newArrayList();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMantleOfTheBovemistsSpawn(AddEntityEvent event) {
        Entity entity = McUtils.mc().level.getEntity(event.getId());
        if (entity == null) return;
        if (!(entity instanceof ArmorStand armorStand)) return;

        Vec3 playerPos = McUtils.player().position();
        Managers.TickScheduler.scheduleLater(
                () -> {
                    // Verify that this is an armor stand holding a Mantle of the Bovemists. This must be ran with
                    // a delay, as inventory contents are set a couple ticks after the entity spawns.
                    ItemStack headItem = armorStand.getItemBySlot(EquipmentSlot.HEAD);
                    if (!Objects.equals(headItem.getItem(), Items.DIAMOND_AXE)) return;

                    // If the player is standing still, the armor stands spawn about 2.1 blocks away
                    // from the player. But if the player moves, it can be up to ~ 4 blocks depending
                    // on walk speed.
                    if (armorStand.position().distanceTo(playerPos) > MANTLE_OF_THE_BOVEMISTS_SEARCH_RADIUS) return;

                    // Save field in local variable to avoid surprises where it is overwritten by null
                    List<Integer> mantleOfTheBovemistsIdsCollector = spawnedMantleOfTheBovemistsIds;
                    // If we're not collecting Mantle of the Bovemists, do nothing.
                    if (mantleOfTheBovemistsIdsCollector == null) return;

                    mantleOfTheBovemistsIdsCollector.add(armorStand.getId());
                },
                3);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMantleOfTheBovemistsDestroy(RemoveEntitiesEvent event) {
        if (spawnedMantleOfTheBovemistsIds == null || spawnedMantleOfTheBovemistsIds.isEmpty()) return;

        for (Integer destroyedEntityId : event.getEntityIds()) {
            spawnedMantleOfTheBovemistsIds.stream()
                    .filter(id -> Objects.equals(id, destroyedEntityId))
                    .findFirst()
                    .ifPresent(id -> {
                        spawnedMantleOfTheBovemistsIds.remove(id);

                        if (spawnedMantleOfTheBovemistsIds == null || spawnedMantleOfTheBovemistsIds.isEmpty()) {
                            // TODO: Add a toggle whether you want the message or not (off by default)
                            McUtils.sendMessageToClient(Component.empty());
                            McUtils.sendMessageToClient(
                                    Component.translatable("sequoia.feature.mantleOfTheBovemistsTracker.allDestroyed"));
                            McUtils.sendMessageToClient(Component.empty());
                        }
                    });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClassChange(CharacterUpdateEvent event) {
        removeMantleOfTheBovemists();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHeldItemChange(ChangeCarriedItemEvent event) {
        removeMantleOfTheBovemists();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldStateChanged(WorldStateEvent event) {
        removeMantleOfTheBovemists();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDisconnected(ConnectionEvent.DisconnectedEvent event) {
        removeMantleOfTheBovemists();
    }

    public int getMantleOfTheBovemistsCharge() {
        return spawnedMantleOfTheBovemistsIds == null ? 0 : spawnedMantleOfTheBovemistsIds.size();
    }

    private void removeMantleOfTheBovemists() {
        spawnedMantleOfTheBovemistsIds.clear();
    }
}
