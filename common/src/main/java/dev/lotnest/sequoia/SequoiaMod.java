/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia;

import com.google.common.collect.Maps;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.mod.type.CrashType;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.core.components.CoreComponent;
import dev.lotnest.sequoia.core.components.Handler;
import dev.lotnest.sequoia.core.components.Handlers;
import dev.lotnest.sequoia.core.components.Manager;
import dev.lotnest.sequoia.core.components.Managers;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.core.components.Models;
import dev.lotnest.sequoia.core.components.Service;
import dev.lotnest.sequoia.core.components.Services;
import dev.lotnest.sequoia.core.events.SequoiaCrashEvent;
import dev.lotnest.sequoia.core.http.HttpClient;
import dev.lotnest.sequoia.core.persisted.SequoiaConfig;
import dev.lotnest.sequoia.core.text.Fonts;
import dev.lotnest.sequoia.features.ws.WebSocketFeature;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SequoiaMod {
    public static final String MOD_ID = "sequoia";
    public static final SequoiaConfig CONFIG = SequoiaConfig.createAndLoad();

    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final MutableComponent PREFIX = Component.empty()
            .append(Fonts.BannerPill.parse(MOD_ID).withStyle(style -> style.withColor(0x19A775)))
            .append(Component.literal(" » ")
                    .withStyle(style -> style.withColor(ChatFormatting.GRAY).withBold(false)))
            .append(Component.empty().withStyle(ChatFormatting.YELLOW));

    private static final Map<Class<? extends CoreComponent>, List<CoreComponent>> componentMap = Maps.newHashMap();
    private static ModLoader modLoader;
    private static String version = "";
    private static int versionInt = 0;
    private static boolean isDevelopmentBuild = false;
    private static boolean isDevelopmentEnvironment = false;
    private static boolean isInitCompleted = false;

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

    public static void debug(String message) {
        if (CONFIG.verboseLogging()) {
            LOGGER.info("[VERBOSE] {}", message);
        }
    }

    // Ran when resources (including I18n) are available
    public static void onResourcesFinishedLoading() {
        if (isInitCompleted) {
            return;
        }
        isInitCompleted = true;

        try {
            registerComponents(Managers.class, Manager.class);
            registerComponents(Handlers.class, Handler.class);
            registerComponents(Models.class, Model.class);
            registerComponents(Services.class, Service.class);

            addCrashCallbacks();

            initFeatures();
        } catch (Exception exception) {
            LOGGER.error("Failed to initialize Sequoia components", exception);
        }
    }

    public static void init(ModLoader modLoader, boolean isDevelopmentEnvironment, String modVersion) {
        // Note that at this point, no resources (including I18n) are available, so we postpone features until then
        SequoiaMod.modLoader = modLoader;
        isDevelopmentBuild = modVersion.contains("SNAPSHOT");
        SequoiaMod.isDevelopmentEnvironment = isDevelopmentEnvironment;
        version = "v" + modVersion;
        versionInt = Integer.parseInt(modVersion.replaceAll("\\D", ""));
        HttpClient httpClient = HttpClient.newHttpClient();

        LOGGER.info(
                "Sequoia: Starting version {} (using {} on Minecraft {})",
                version,
                modLoader,
                SharedConstants.getCurrentVersion().getName());
    }

    private static void registerComponents(Class<?> registryClass, Class<? extends CoreComponent> componentClass) {
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
                    }
                });
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

    public static MutableComponent prefix(Component component) {
        return PREFIX.copy().append(component);
    }

    public static ModLoader getModLoader() {
        return modLoader;
    }

    public static String getVersion() {
        return version;
    }

    public static int getVersionInt() {
        return versionInt;
    }

    public static boolean isDevelopmentBuild() {
        return isDevelopmentBuild;
    }

    public static boolean isDevelopmentEnvironment() {
        return isDevelopmentEnvironment;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static WebSocketFeature getWebSocketFeature() {
        return Managers.Feature.getFeatureInstance(WebSocketFeature.class);
    }

    public enum ModLoader {
        FORGE,
        FABRIC
    }
}
