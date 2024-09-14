package dev.lotnest.sequoia.utils;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;

public class WynncraftUtils {
    private static final UUID WC_LIST_ENTRY = UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af");

    private WynncraftUtils() {}

    public static String getWC() {
        return Minecraft.getInstance().player.connection.getListedOnlinePlayers().stream()
                .filter(playerInfo -> Objects.equals(playerInfo.getProfile().getId(), WC_LIST_ENTRY))
                .findFirst()
                .map(playerInfo -> playerInfo.getProfile().getName())
                .orElse("");
    }
}
