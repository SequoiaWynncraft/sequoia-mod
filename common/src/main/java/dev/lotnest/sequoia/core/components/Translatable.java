/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.components;

import com.google.common.base.CaseFormat;
import java.util.Locale;
import net.minecraft.client.resources.language.I18n;

public interface Translatable {
    String getTypeName();

    default String getTranslation(String keySuffix) {
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
