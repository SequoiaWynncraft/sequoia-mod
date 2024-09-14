package dev.lotnest.sequoia.overlay;

import com.wynntils.core.components.Managers;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.consumers.overlays.Overlay;
import com.wynntils.core.consumers.overlays.OverlayManager;
import com.wynntils.core.consumers.overlays.RenderState;
import com.wynntils.mc.event.RenderEvent;
import dev.lotnest.sequoia.feature.WynntilsFeatureInjector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class WynntilsOverlayInjector {
    private WynntilsOverlayInjector() {}

    public static void injectOverlays()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method registerOverlayMethod = OverlayManager.class.getDeclaredMethod(
                "registerOverlay",
                Overlay.class,
                Feature.class,
                RenderEvent.ElementType.class,
                RenderState.class,
                boolean.class);

        registerOverlayMethod.setAccessible(true);
        registerOverlayMethod.invoke(
                Managers.Overlay,
                WynntilsFeatureInjector.MantleOfTheBovemistsTrackerOverlayFeature.mantleOfTheBovemistsTrackerOverlay,
                WynntilsFeatureInjector.MantleOfTheBovemistsTrackerOverlayFeature,
                RenderEvent.ElementType.GUI,
                RenderState.POST,
                true);
    }
}
