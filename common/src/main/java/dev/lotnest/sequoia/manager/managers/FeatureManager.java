package dev.lotnest.sequoia.manager.managers;

import com.google.common.collect.Maps;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.consumers.features.FeatureState;
import com.wynntils.core.mod.type.CrashType;
import com.wynntils.mc.event.ClientsideMessageEvent;
import com.wynntils.mc.event.CommandsAddedEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.feature.FeatureCommands;
import dev.lotnest.sequoia.feature.features.CommandsFeature;
import dev.lotnest.sequoia.feature.features.PlayerIgnoreFeature;
import dev.lotnest.sequoia.feature.features.SequoiaOSTFeature;
import dev.lotnest.sequoia.feature.features.discordchatbridge.DiscordChatBridgeFeature;
import dev.lotnest.sequoia.feature.features.guildmessagefilter.GuildMessageFilterFeature;
import dev.lotnest.sequoia.feature.features.guildraidtracker.GuildRaidTrackerFeature;
import dev.lotnest.sequoia.feature.features.lootpool.LootPoolTrackerFeature;
import dev.lotnest.sequoia.manager.Manager;
import dev.lotnest.sequoia.manager.Managers;
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
        registerFeature(new GuildMessageFilterFeature());
        registerFeature(new PlayerIgnoreFeature());
        registerFeature(new SequoiaOSTFeature());
        registerFeature(new GuildRaidTrackerFeature());
        registerFeature(new DiscordChatBridgeFeature());
        registerFeature(new LootPoolTrackerFeature());

        // Reload Minecraft's config files so our own keybinds get loaded
        // This is needed because we are late to register the keybinds,
        // but we cannot move it earlier to the init process because of I18n
        synchronized (McUtils.options()) {
            McUtils.options().load();
        }

        commands.init();

        addCrashCallbacks();
    }

    private void registerFeature(Feature feature) {
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
        commands.discoverCommands(feature);

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

        if (state != FeatureState.DISABLED && state != FeatureState.CRASHED) return;

        feature.onEnable();

        FEATURES.put(feature, FeatureState.ENABLED);

        WynntilsMod.registerEventListener(feature);
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
    }

    public void crashFeature(Feature feature) {
        if (!FEATURES.containsKey(feature)) {
            throw new IllegalArgumentException("Tried to crash an unregistered feature: " + feature);
        }

        disableFeature(feature);

        FEATURES.put(feature, FeatureState.CRASHED);
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

    public void handleExceptionInEventListener(Event event, String featureClassName, Throwable t) {
        String featureName = featureClassName.substring(featureClassName.lastIndexOf('.') + 1);

        Optional<Feature> featureOptional = getFeatureFromString(featureName);
        if (featureOptional.isEmpty()) {
            SequoiaMod.error("Exception in event listener in feature that cannot be located: " + featureClassName, t);
            return;
        }

        Feature feature = featureOptional.get();

        crashFeature(feature);

        // If a crash happens in a client-side message event, and we send a new message about disabling X feature,
        // we will cause a new exception and an endless recursion.
        boolean shouldSendChat = !(event instanceof ClientsideMessageEvent);

        SequoiaMod.reportCrash(
                CrashType.FEATURE,
                feature.getTranslatedName(),
                feature.getClass().getName(),
                "event listener",
                shouldSendChat,
                true,
                t);

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
