package dev.lotnest.sequoia.wynn;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;

public final class WynnUtils {
    private static final UUID WC_LIST_ENTRY = UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af");
    private static final Pattern WYNNCRAFT_SERVER_PATTERN =
            Pattern.compile("^(?:(.*)\\.)?wynncraft\\.(?:com|net|org)$");
    private static final Pattern WORLD_NAME = Pattern.compile("^§f {2}§lGlobal \\[(.*)]$");

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

    public static boolean isWynncraftServer(String host) {
        return WYNNCRAFT_SERVER_PATTERN.matcher(host).matches();
    }

    public static boolean isWynncraftWorld(String input) {
        return WORLD_NAME.matcher(input).matches();
    }
}
