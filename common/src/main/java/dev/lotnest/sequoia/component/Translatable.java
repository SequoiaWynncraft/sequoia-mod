package dev.lotnest.sequoia.component;

import com.google.common.base.CaseFormat;
import java.util.Locale;
import net.minecraft.client.resources.language.I18n;

public interface Translatable {
    String getTypeName();

    default String getTranslation(String keySuffix) {
        // This needed to force Java to select the varargs overload
        return getTranslation(keySuffix, new Object[0]);
    }

    default String getTranslation(String keySuffix, Object... parameters) {
        return I18n.get(
                "sequoia." + getTypeName().toLowerCase(Locale.ROOT) + "." + getTranslationKeyName() + "." + keySuffix,
                parameters);
    }

    default String getTranslationKeyName() {
        String name = this.getClass().getSimpleName().replace(getTypeName(), "");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
    }

    default String getTranslatedName() {
        return getTranslation("name");
    }
}
