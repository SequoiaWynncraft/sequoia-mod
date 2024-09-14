package dev.lotnest.sequoia.feature;

import com.wynntils.core.components.Managers;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.consumers.features.FeatureManager;
import dev.lotnest.sequoia.feature.features.MantleOfTheBovemistsTrackerOverlayFeature;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class WynntilsFeatureInjector {
    public static final MantleOfTheBovemistsTrackerOverlayFeature MantleOfTheBovemistsTrackerOverlayFeature =
            new MantleOfTheBovemistsTrackerOverlayFeature();

    private WynntilsFeatureInjector() {}

    public static void injectFeatures()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method registerFeatureMethod = FeatureManager.class.getDeclaredMethod("registerFeature", Feature.class);

        registerFeatureMethod.setAccessible(true);
        registerFeatureMethod.invoke(Managers.Feature, MantleOfTheBovemistsTrackerOverlayFeature);
    }
}
