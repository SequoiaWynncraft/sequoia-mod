/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class Fonts {
    private Fonts() {}

    public static class Default {
        public static final ResourceLocation RESOURCE_LOCATION =
                ResourceLocation.fromNamespaceAndPath("minecraft", "default");

        private Default() {}

        public static MutableComponent parse(String text) {
            return Component.literal(text).setStyle(Style.EMPTY.withFont(RESOURCE_LOCATION));
        }
    }

    public static class BannerPill {
        public static final ResourceLocation RESOURCE_LOCATION =
                ResourceLocation.fromNamespaceAndPath("minecraft", "banner/pill");

        private BannerPill() {}

        public static MutableComponent parse(String text) {
            StringBuilder finalText = new StringBuilder("\uE060\uDAFF\uDFFF");
            for (int i = 0; i < text.length(); i++) {
                int index = Character.toLowerCase(text.charAt(i)) - 'a';
                finalText.append(Character.toChars(index + 57392));
                finalText.append("\uDAFF\uDFFF");
            }
            finalText.append("\uE062");
            return Component.literal(finalText.toString()).setStyle(Style.EMPTY.withFont(RESOURCE_LOCATION));
        }
    }
}
