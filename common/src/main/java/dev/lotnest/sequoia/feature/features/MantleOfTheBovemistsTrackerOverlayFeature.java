package dev.lotnest.sequoia.feature.features;

import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.consumers.overlays.Overlay;
import com.wynntils.core.consumers.overlays.annotations.OverlayInfo;
import com.wynntils.core.persisted.config.Category;
import com.wynntils.core.persisted.config.ConfigCategory;
import com.wynntils.mc.event.RenderEvent;
import dev.lotnest.sequoia.overlay.overlays.MantleOfTheBovemistsTrackerOverlay;

@ConfigCategory(Category.OVERLAYS)
public class MantleOfTheBovemistsTrackerOverlayFeature extends Feature {
    @OverlayInfo(renderType = RenderEvent.ElementType.GUI)
    public final Overlay mantleOfTheBovemistsTrackerOverlay = new MantleOfTheBovemistsTrackerOverlay();

    @Override
    public String getShortName() {
        return "Mantle of the Bovemists Tracker";
    }

    @Override
    public String getTranslatedName() {
        return "Mantle of the Bovemists Tracker";
    }

    @Override
    public String getTranslatedDescription() {
        return "Displays your Mantle of the Bovemists count on the screen";
    }
}
