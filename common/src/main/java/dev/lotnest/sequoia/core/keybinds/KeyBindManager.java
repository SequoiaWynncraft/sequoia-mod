/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.keybinds;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.mojang.blaze3d.platform.InputConstants;
import com.wynntils.core.mod.type.CrashType;
import com.wynntils.mc.event.InventoryKeyPressEvent;
import com.wynntils.mc.event.InventoryMouseClickedEvent;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.mc.mixin.accessors.OptionsAccessor;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Manager;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.core.consumers.features.properties.RegisterKeyBind;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.reflect.FieldUtils;

public final class KeyBindManager extends Manager {
    private final Set<KeyBind> enabledKeyBinds = ConcurrentHashMap.newKeySet();
    private final Map<Feature, List<Pair<KeyBind, String>>> keyBinds = new ConcurrentHashMap<>();

    public KeyBindManager() {
        super(List.of());
    }

    public void discoverKeyBinds(Feature feature) {
        for (Field field : FieldUtils.getFieldsWithAnnotation(feature.getClass(), RegisterKeyBind.class)) {
            if (!field.getType().equals(KeyBind.class)) {
                continue;
            }

            try {
                KeyBind keyBind = (KeyBind) FieldUtils.readField(field, feature, true);
                keyBinds.putIfAbsent(feature, Lists.newLinkedList());
                keyBinds.get(feature).add(Pair.of(keyBind, field.getName()));
            } catch (Exception exception) {
                SequoiaMod.error(
                        "Failed to register KeyBind " + field.getName() + " in "
                                + feature.getClass().getName(),
                        exception);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        triggerKeybinds();
    }

    @SubscribeEvent
    public void onKeyPress(InventoryKeyPressEvent event) {
        checkAllKeyBinds(keyBind -> {
            if (keyBind.getKeyMapping().matches(event.getKeyCode(), event.getScanCode())) {
                keyBind.onInventoryPress(event.getHoveredSlot());
            }
        });
    }

    @SubscribeEvent
    public void onMousePress(InventoryMouseClickedEvent event) {
        checkAllKeyBinds(keyBind -> {
            if (keyBind.getKeyMapping().matchesMouse(event.getButton())) {
                keyBind.onInventoryPress(event.getHoveredSlot());
            }
        });
    }

    public void enableFeatureKeyBinds(Feature feature) {
        if (!keyBinds.containsKey(feature)) {
            return;
        }

        for (Pair<KeyBind, String> keyBind : keyBinds.get(feature)) {
            registerKeybind(feature, keyBind.getKey(), keyBind.getValue());
        }
    }

    public void disableFeatureKeyBinds(Feature feature) {
        if (!keyBinds.containsKey(feature)) return;

        for (Pair<KeyBind, String> keyBind : keyBinds.get(feature)) {
            unregisterKeybind(feature, keyBind.getKey());
        }
    }

    private void registerKeybind(Feature parent, KeyBind toAdd, String fieldName) {
        if (hasName(toAdd.getName())) {
            throw new IllegalStateException(
                    "Can not add keybind " + toAdd.getName() + " since the name already exists");
        }

        KeyMapping keyMapping = toAdd.getKeyMapping();

        synchronized (McUtils.options()) {
            enabledKeyBinds.add(toAdd);

            Options options = McUtils.options();
            KeyMapping[] keyMappings = options.keyMappings;

            List<KeyMapping> newKeyMappings = Lists.newArrayList(keyMappings);
            newKeyMappings.add(keyMapping);

            ((OptionsAccessor) options).setKeyBindMixins(newKeyMappings.toArray(KeyMapping[]::new));
        }

        keyMapping.setKey(keyMapping.getDefaultKey());
        KeyMapping.resetMapping();
    }

    private void unregisterKeybind(Feature parent, KeyBind toRemove) {
        if (!enabledKeyBinds.remove(toRemove)) {
            return;
        }

        KeyMapping keyMapping = toRemove.getKeyMapping();

        synchronized (McUtils.options()) {
            Options options = McUtils.options();
            KeyMapping[] keyMappings = options.keyMappings;

            List<KeyMapping> newKeyMappings = Lists.newArrayList(keyMappings);
            newKeyMappings.remove(toRemove.getKeyMapping());

            ((OptionsAccessor) options).setKeyBindMixins(newKeyMappings.toArray(KeyMapping[]::new));
        }

        keyMapping.setKey(InputConstants.UNKNOWN);
        KeyMapping.resetMapping();
    }

    private void triggerKeybinds() {
        checkAllKeyBinds(keyBind -> {
            if (keyBind.onlyFirstPress()) {
                if (keyBind.getKeyMapping().isDown() && !keyBind.isPressed()) {
                    keyBind.onPress();
                }

                keyBind.setIsPressed(keyBind.getKeyMapping().isDown());
            } else if (keyBind.getKeyMapping().isDown()) {
                keyBind.onPress();
            }
        });
    }

    private void checkAllKeyBinds(Consumer<KeyBind> checkKeybind) {
        List<Pair<Feature, KeyBind>> crashedKeyBinds = Lists.newLinkedList();

        for (Map.Entry<Feature, List<Pair<KeyBind, String>>> entry : keyBinds.entrySet()) {
            Feature parent = entry.getKey();

            for (Pair<KeyBind, String> keyBind : entry.getValue()) {
                try {
                    checkKeybind.accept(keyBind.getKey());
                } catch (Throwable throwable) {
                    // We can't disable it right away since that will cause ConcurrentModificationException
                    crashedKeyBinds.add(Pair.of(parent, keyBind.getKey()));

                    SequoiaMod.reportCrash(
                            CrashType.KEYBIND,
                            keyBind.getValue(),
                            parent.getClass().getName() + "." + keyBind.getValue(),
                            "handling",
                            throwable);
                }
            }
        }

        for (Pair<Feature, KeyBind> keyBindPair : crashedKeyBinds) {
            unregisterKeybind(keyBindPair.getKey(), keyBindPair.getValue());
        }
    }

    private boolean hasName(String name) {
        return enabledKeyBinds.stream().anyMatch(k -> k.getName().equals(name));
    }

    /**
     * Note: this is called directly from a mixin!
     */
    public static void initKeyMapping(String category, Map<String, Integer> categorySortOrder) {
        if (categorySortOrder.containsKey(category)) {
            return;
        }

        int max = 0;

        for (int val : categorySortOrder.values()) {
            if (val > max) {
                max = val;
            }
        }

        categorySortOrder.put(category, max + 1);
    }
}
