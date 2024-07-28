package dev.lotnest.sequoia.feature;

import java.util.Locale;
import net.minecraft.client.resources.language.I18n;

public enum CategoryType {
    UNCATEGORIZED,
    CHAT,
    COMBAT,
    COMMANDS,
    DEBUG,
    EMBELLISHMENTS,
    INVENTORY,
    MAP,
    OVERLAYS,
    PLAYERS,
    REDIRECTS,
    TOOLTIPS,
    TRADEMARKET,
    UI,
    UTILITIES,
    SEQUOIA;

    @Override
    public String toString() {
        return I18n.get("sequoia.feature.categoryType." + this.name().toLowerCase(Locale.ROOT));
    }
}
