package dev.lotnest.sequoia;

import com.google.common.collect.Maps;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.mod.type.CrashType;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.component.CoreComponent;
import dev.lotnest.sequoia.configs.SequoiaConfig;
import dev.lotnest.sequoia.events.SequoiaCrashEvent;
import dev.lotnest.sequoia.feature.WynntilsFeatureInjector;
import dev.lotnest.sequoia.function.WynntilsFunctionInjector;
import dev.lotnest.sequoia.manager.Manager;
import dev.lotnest.sequoia.manager.Managers;
import dev.lotnest.sequoia.overlay.WynntilsOverlayInjector;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.SharedConstants;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SequoiaMod {
    public static final String MOD_ID = "sequoia";
    public static final SequoiaConfig CONFIG = SequoiaConfig.createAndLoad();

    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModLoader modLoader;
    private static String version = "";
    private static boolean developmentBuild = false;
    private static boolean developmentEnvironment;
    private static boolean initCompleted = false;
    private static final Map<Class<? extends CoreComponent>, List<CoreComponent>> componentMap = Maps.newHashMap();

    public static String getVersion() {
        return version;
    }

    public static boolean isDevelopmentBuild() {
        return developmentBuild;
    }

    public static boolean isDevelopmentEnvironment() {
        return developmentEnvironment;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void error(String message, Throwable t) {
        LOGGER.error(message, t);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }

    public static void warn(String message, Throwable throwable) {
        LOGGER.warn(message, throwable);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    // Ran when resources (including I18n) are available
    public static void onResourcesFinishedLoading() {
        if (initCompleted) return;
        initCompleted = true;

        try {
            registerComponents(Managers.class, Manager.class);

            addCrashCallbacks();

            initFeatures();

            WynntilsFeatureInjector.injectFeatures();
            WynntilsFunctionInjector.injectFunctions();
            WynntilsOverlayInjector.injectOverlays();
        } catch (Throwable throwable) {
            LOGGER.error("Failed to initialize Sequoia components", throwable);
        }
    }

    public static void init(ModLoader modLoader, String modVersion, boolean isDevelopmentEnvironment) {
        // Note that at this point, no resources (including I18n) are available, so we postpone features until then
        SequoiaMod.modLoader = modLoader;
        developmentEnvironment = isDevelopmentEnvironment;

        parseVersion(modVersion);

        LOGGER.info(
                "Sequoia: Starting version {} (using {} on Minecraft {})",
                version,
                modLoader,
                SharedConstants.getCurrentVersion().getName());
    }

    private static void registerComponents(Class<?> registryClass, Class<? extends CoreComponent> componentClass) {
        // Register all handler singletons as event listeners
        List<CoreComponent> components = componentMap.computeIfAbsent(componentClass, k -> new ArrayList<>());

        FieldUtils.getAllFieldsList(registryClass).stream()
                .filter(field -> componentClass.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    try {
                        CoreComponent component = (CoreComponent) field.get(null);
                        WynntilsMod.registerEventListener(component);
                        components.add(component);
                    } catch (IllegalAccessException exception) {
                        error("Internal error in " + registryClass.getSimpleName(), exception);
                        throw new RuntimeException(exception);
                    }
                });
    }

    private static void parseVersion(String modVersion) {
        developmentBuild = modVersion.contains("SNAPSHOT");
        version = "v" + modVersion;
    }

    private static void initFeatures() {
        // Init all features. Resources (i.e I18n) are now available.
        Managers.Feature.init();

        LOGGER.info(
                "Sequoia: {} features are now loaded and ready",
                Managers.Feature.getFeatures().size());
    }

    private static void addCrashCallbacks() {
        Managers.CrashReport.registerCrashContext("In Development", () -> isDevelopmentEnvironment() ? "Yes" : "No");
    }

    public static void reportCrash(
            CrashType type, String niceName, String fullName, String reason, Throwable throwable) {
        reportCrash(type, niceName, fullName, reason, true, true, throwable);
    }

    public static void reportCrash(
            CrashType type,
            String niceName,
            String fullName,
            String reason,
            boolean shouldSendChat,
            boolean isDisabled,
            Throwable throwable) {
        warn("Disabling " + type.toString().toLowerCase(Locale.ROOT) + " " + niceName + " due to " + reason);
        error("Exception thrown by " + fullName, throwable);

        if (shouldSendChat) {
            McUtils.sendErrorToClient("Sequoia error: " + type.getName() + " '" + niceName + "' has crashed in "
                    + reason + (isDisabled ? " and has been disabled" : ""));
        }

        WynntilsMod.postEvent(new SequoiaCrashEvent(fullName, type, throwable));
    }

    public enum ModLoader {
        FORGE,
        FABRIC
    }
}
