/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.consumers.features;

import com.google.common.collect.Maps;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.consumers.features.FeatureState;
import com.wynntils.core.mod.type.CrashType;
import com.wynntils.mc.event.ClientsideMessageEvent;
import com.wynntils.mc.event.CommandsAddedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Manager;
import dev.lotnest.sequoia.core.components.Managers;
import dev.lotnest.sequoia.core.consumers.command.FeatureCommands;
import dev.lotnest.sequoia.features.CommandsFeature;
import dev.lotnest.sequoia.features.GuildRewardStorageFullAlertFeature;
import dev.lotnest.sequoia.features.OuterVoidTrackerFeature;
import dev.lotnest.sequoia.features.PlayerIgnoreFeature;
import dev.lotnest.sequoia.features.SequoiaOSTFeature;
import dev.lotnest.sequoia.features.WebSocketFeature;
import dev.lotnest.sequoia.features.discordchatbridge.DiscordChatBridgeFeature;
import dev.lotnest.sequoia.features.guildraidtracker.GuildRaidTrackerFeature;
import dev.lotnest.sequoia.features.messagefilter.MessageFilterFeature;
import dev.lotnest.sequoia.features.messagefilter.guild.GuildMessageFilterFeature;
import dev.lotnest.sequoia.features.messagefilter.mod.ModMessageFilterFeature;
import dev.lotnest.sequoia.features.raids.NOLRaidFeature;
import dev.lotnest.sequoia.features.raids.PartyRaidCompletionsDisplayFeature;
import dev.lotnest.sequoia.features.raids.RaidsFeature;
import dev.lotnest.sequoia.features.raids.TNARaidFeature;
import dev.lotnest.sequoia.features.territory.TerritoryFeature;
import dev.lotnest.sequoia.features.territory.TerritoryMenuHotkeyFeature;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public final class FeatureManager extends Manager {
    private static final Map<Feature, FeatureState> FEATURES = Maps.newLinkedHashMap();
    private static final Map<Class<? extends Feature>, Feature> FEATURE_INSTANCES = Maps.newLinkedHashMap();

    private final FeatureCommands commands = new FeatureCommands();

    public FeatureManager() {
        super(List.of());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommandsAdded(CommandsAddedEvent event) {
        Managers.Command.addNode(event.getRoot(), commands.getCommandNode());
    }

    public void init() {
        registerFeature(new CommandsFeature());
        registerFeature(new PlayerIgnoreFeature());
        registerFeature(new SequoiaOSTFeature());
        registerFeature(new WebSocketFeature());
        registerFeature(new GuildRaidTrackerFeature());
        registerFeature(new DiscordChatBridgeFeature());
        registerFeature(new MessageFilterFeature());
        registerFeature(new GuildMessageFilterFeature());
        registerFeature(new ModMessageFilterFeature());
        registerFeature(new OuterVoidTrackerFeature());
        registerFeature(new RaidsFeature());
        registerFeature(new NOLRaidFeature());
        registerFeature(new TNARaidFeature());
        registerFeature(new PartyRaidCompletionsDisplayFeature());
        registerFeature(new TerritoryFeature());
        registerFeature(new TerritoryMenuHotkeyFeature());
        registerFeature(new GuildRewardStorageFullAlertFeature());

        synchronized (McUtils.options()) {
            McUtils.options().load();
        }

        commands.init();

        addCrashCallbacks();
    }

    private void registerFeature(Feature feature) {
        SequoiaMod.debug("Registering feature: " + feature.getClass().getSimpleName());

        FEATURES.put(feature, FeatureState.DISABLED);
        FEATURE_INSTANCES.put(feature.getClass(), feature);

        try {
            initializeFeature(feature);
        } catch (AssertionError ae) {
            SequoiaMod.error("Fix i18n for " + feature.getClass().getSimpleName(), ae);
            if (SequoiaMod.isDevelopmentEnvironment()) {
                System.exit(1);
            }
        } catch (Throwable exception) {
            crashFeature(feature);
            SequoiaMod.reportCrash(
                    CrashType.FEATURE,
                    feature.getClass().getSimpleName(),
                    feature.getClass().getName(),
                    "init",
                    false,
                    true,
                    exception);
        }
    }

    private void initializeFeature(Feature feature) {
        SequoiaMod.debug("Initializing feature: " + feature.getClass().getSimpleName());

        commands.discoverCommands(feature);
        Managers.KeyBind.discoverKeyBinds(feature);

        assert !feature.getTranslatedName().startsWith("sequoia.feature.")
                : "Fix i18n for " + feature.getTranslatedName();

        assert !feature.getTranslatedDescription().startsWith("sequoia.feature.")
                : "Fix i18n for " + feature.getTranslatedDescription();

        enableFeature(feature);
    }

    public void enableFeature(Feature feature) {
        if (!FEATURES.containsKey(feature)) {
            throw new IllegalArgumentException("Tried to enable an unregistered feature: " + feature);
        }

        FeatureState state = FEATURES.get(feature);

        if (state != FeatureState.DISABLED && state != FeatureState.CRASHED) {
            return;
        }

        feature.onEnable();

        FEATURES.put(feature, FeatureState.ENABLED);

        WynntilsMod.registerEventListener(feature);

        Managers.KeyBind.enableFeatureKeyBinds(feature);

        SequoiaMod.debug("Enabled feature: " + feature.getClass().getSimpleName());
    }

    public void disableFeature(Feature feature) {
        if (!FEATURES.containsKey(feature)) {
            throw new IllegalArgumentException("Tried to disable an unregistered feature: " + feature);
        }

        FeatureState state = FEATURES.get(feature);

        if (state != FeatureState.ENABLED) return;

        feature.onDisable();

        FEATURES.put(feature, FeatureState.DISABLED);

        WynntilsMod.unregisterEventListener(feature);

        Managers.KeyBind.disableFeatureKeyBinds(feature);

        SequoiaMod.debug("Disabled feature: " + feature.getClass().getSimpleName());
    }

    public void crashFeature(Feature feature) {
        if (!FEATURES.containsKey(feature)) {
            throw new IllegalArgumentException("Tried to crash an unregistered feature: " + feature);
        }

        disableFeature(feature);

        FEATURES.put(feature, FeatureState.CRASHED);

        SequoiaMod.debug("Crashed feature: " + feature.getClass().getSimpleName());
    }

    private FeatureState getFeatureState(Feature feature) {
        if (!FEATURES.containsKey(feature)) {
            throw new IllegalArgumentException(
                    "Feature " + feature + " is not registered, but was was queried for its state");
        }

        return FEATURES.get(feature);
    }

    public boolean isEnabled(Feature feature) {
        return getFeatureState(feature) == FeatureState.ENABLED;
    }

    public List<Feature> getFeatures() {
        return FEATURES.keySet().stream().toList();
    }

    @SuppressWarnings("unchecked")
    public <T extends Feature> T getFeatureInstance(Class<T> featureClass) {
        return (T) FEATURE_INSTANCES.get(featureClass);
    }

    public Optional<Feature> getFeatureFromString(String featureName) {
        return getFeatures().stream()
                .filter(feature -> feature.getShortName().equals(featureName))
                .findFirst();
    }

    public void handleExceptionInEventListener(Event event, String featureClassName, Throwable throwable) {
        String featureName = featureClassName.substring(featureClassName.lastIndexOf('.') + 1);

        Optional<Feature> featureOptional = getFeatureFromString(featureName);
        if (featureOptional.isEmpty()) {
            SequoiaMod.error(
                    "Exception in event listener in feature that cannot be located: " + featureClassName, throwable);
            return;
        }

        Feature feature = featureOptional.get();

        crashFeature(feature);

        boolean shouldSendChat = !(event instanceof ClientsideMessageEvent);

        SequoiaMod.reportCrash(
                CrashType.FEATURE,
                feature.getTranslatedName(),
                feature.getClass().getName(),
                "event listener",
                shouldSendChat,
                true,
                throwable);

        if (shouldSendChat) {
            MutableComponent enableMessage = Component.literal("Click here to enable it again.")
                    .withStyle(ChatFormatting.UNDERLINE)
                    .withStyle(ChatFormatting.RED)
                    .withStyle(style -> style.withClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND, "/feature enable " + feature.getShortName())));

            McUtils.sendMessageToClient(enableMessage);
        }
    }

    private void addCrashCallbacks() {
        Managers.CrashReport.registerCrashContext("Loaded Features", () -> {
            StringBuilder result = new StringBuilder();

            for (Feature feature : FEATURES.keySet()) {
                if (feature.isEnabled()) {
                    result.append("\n\t\t").append(feature.getTranslatedName());
                }
            }

            return result.toString();
        });
    }
}
