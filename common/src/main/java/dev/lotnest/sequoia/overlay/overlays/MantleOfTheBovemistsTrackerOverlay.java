package dev.lotnest.sequoia.overlay.overlays;

import com.wynntils.core.consumers.overlays.OverlayPosition;
import com.wynntils.core.consumers.overlays.OverlaySize;
import com.wynntils.core.consumers.overlays.TextOverlay;
import com.wynntils.core.persisted.Persisted;
import com.wynntils.core.persisted.config.Config;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.VerticalAlignment;

public class MantleOfTheBovemistsTrackerOverlay extends TextOverlay {
    private static final String SHIELD_SYMBOL = " 🛡";

    private static final String TEMPLATE =
            "{IF_STRING(GT(MANTLE_OF_THE_BOVEMISTS_COUNT; 0); CONCAT(REPEAT(\"%s\"; MANTLE_OF_THE_BOVEMISTS_COUNT)); \"\")}"
                    .formatted(SHIELD_SYMBOL);

    @Persisted(i18nKey = "feature.wynntils.gameBarsOverlay.overlay.baseBar.textColor")
    private final Config<CustomColor> textColor = new Config<>(CommonColors.CYAN);

    public MantleOfTheBovemistsTrackerOverlay() {
        super(
                new OverlayPosition(
                        -10,
                        0,
                        VerticalAlignment.TOP,
                        HorizontalAlignment.CENTER,
                        OverlayPosition.AnchorSection.MIDDLE),
                new OverlaySize(150, 20),
                HorizontalAlignment.CENTER,
                VerticalAlignment.MIDDLE);
        fontScale.store(2F);
    }

    @Override
    public String getTranslatedName() {
        return "Mantle of the Bovemists Tracker";
    }

    @Override
    public String getShortName() {
        return "Mantle of the Bovemists Tracker";
    }

    @Override
    public CustomColor getRenderColor() {
        return textColor.get();
    }

    @Override
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getPreviewTemplate() {
        return "Mantle of the Bovemists: {REPEAT(\"%s\"; 3)}".formatted(SHIELD_SYMBOL);
    }
}
