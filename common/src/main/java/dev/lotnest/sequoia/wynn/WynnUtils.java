package dev.lotnest.sequoia.wynn;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;

public class WynnUtils {
    private static final UUID WC_LIST_ENTRY = UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af");

    private WynnUtils() {}

    public static String getWC() {
        return Minecraft.getInstance().player.connection.getListedOnlinePlayers().stream()
                .filter(playerInfo -> Objects.equals(playerInfo.getProfile().getId(), WC_LIST_ENTRY))
                .findFirst()
                .map(playerInfo -> playerInfo.getProfile().getName())
                .orElse("");
    }

    public static String getUnformattedString(String string) {
        return string.replaceAll("\udaff\udffc\ue006\udaff\udfff\ue002\udaff\udffe", "")
                .replaceAll("\udaff\udffc\ue001\udb00\udc06", "")
                .replaceAll("§.", "")
                .replaceAll("&.", "")
                .replaceAll("\\[[0-9:]+]", "")
                .replaceAll("\\s+", " ")
                .replaceAll("\\n", "")
                .replaceAll("[^\\x20-\\x7E]", "")
                .trim();
    }
}
