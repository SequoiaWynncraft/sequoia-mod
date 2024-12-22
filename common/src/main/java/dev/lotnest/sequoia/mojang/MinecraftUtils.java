package dev.lotnest.sequoia.mojang;

import java.util.regex.Pattern;

public final class MinecraftUtils {
    private static final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,16}");

    private MinecraftUtils() {}

    public static boolean isValidUsername(String username) {
        return MINECRAFT_NAME_PATTERN.matcher(username).matches();
    }
}
