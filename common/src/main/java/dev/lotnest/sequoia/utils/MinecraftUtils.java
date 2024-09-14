package dev.lotnest.sequoia.utils;

import com.wynntils.utils.mc.McUtils;

public class MinecraftUtils {
    private MinecraftUtils() {}

    public static void sendCommand(String command) {
        McUtils.player().connection.sendCommand(command);
    }
}
