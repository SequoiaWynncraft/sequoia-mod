package dev.lotnest.sequoia.feature;

import java.util.Locale;
import net.minecraft.client.resources.language.I18n;

public enum CategoryType {
    CHAT,
    COMBAT,
    COMMANDS,
    SOUNDS,
    TRACKERS,
    UNCATEGORIZED;

    @Override
    public String toString() {
        return I18n.get("sequoia.feature.categoryType." + this.name().toLowerCase(Locale.ROOT));
    }
}
