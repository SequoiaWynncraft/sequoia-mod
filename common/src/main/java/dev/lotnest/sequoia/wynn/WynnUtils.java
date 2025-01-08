package dev.lotnest.sequoia.wynn;

import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.minecraft.MinecraftUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public final class WynnUtils {
    private static final Pattern WYNNCRAFT_SERVER_PATTERN =
            Pattern.compile("^(?:(.*)\\.)?wynncraft\\.(?:com|net|org)$");

    private static final UUID WORLD_LIST_ENTRY = UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af");
    private static final Pattern WORLD_NAME_TABLIST_ENTRY = Pattern.compile("^§f {2}§lGlobal \\[(.*)]$");

    private static final Comparator<PlayerInfo> PLAYER_INFO_COMPARATOR =
            Comparator.comparing(playerInfo -> playerInfo.getProfile().getName(), String::compareToIgnoreCase);

    private static final Pattern GUILD_TABLIST_ENTRY_PATTERN = Pattern.compile("§b§l  Guild");

    private static final Pattern PARTY_TABLIST_ENTRY_PATTERN = Pattern.compile("§e  §lParty");
    private static final Pattern[] PLAYER_NOT_IN_PARTY_TABLIST_ENTRIES = {
        Pattern.compile("§7Make a party"), Pattern.compile("§7by typing:"), Pattern.compile("§7/party create")
    };

    private WynnUtils() {}

    public static String getWorldFromTablist() {
        return Minecraft.getInstance().player.connection.getListedOnlinePlayers().stream()
                .filter(playerInfo -> Objects.equals(playerInfo.getProfile().getId(), WORLD_LIST_ENTRY))
                .findFirst()
                .map(playerInfo -> playerInfo.getProfile().getName())
                .orElse("");
    }

    private static boolean isValidPartyMemberEntry(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        if (PARTY_TABLIST_ENTRY_PATTERN.matcher(name).matches()) {
            return false;
        }

        if (GUILD_TABLIST_ENTRY_PATTERN.matcher(name).matches()) {
            return false;
        }

        for (Pattern pattern : PLAYER_NOT_IN_PARTY_TABLIST_ENTRIES) {
            if (pattern.matcher(name).matches()) {
                return false;
            }
        }

        return MinecraftUtils.isValidUsernameWithColor(name);
    }

    public static List<String> getTabList() {
        PlayerTabOverlay tabList = McUtils.mc().gui.getTabList();
        return McUtils.player().connection.getListedOnlinePlayers().stream()
                .sorted(PLAYER_INFO_COMPARATOR)
                .map(tabList::getNameForDisplay)
                .map(Component::getString)
                .toList();
    }

    public static List<String> getTabListWithoutEmptyLines() {
        return getTabList().stream().filter(StringUtils::isNotBlank).toList();
    }

    public static List<String> getPartyMembersFromTabList() {
        List<String> tabListWithoutEmptyLines = getTabListWithoutEmptyLines();
        int partyIndex = tabListWithoutEmptyLines.indexOf("§e  §lParty");

        if (partyIndex == -1) {
            return Collections.emptyList();
        }

        return tabListWithoutEmptyLines.subList(partyIndex + 1, tabListWithoutEmptyLines.size()).stream()
                .takeWhile(WynnUtils::isValidPartyMemberEntry)
                .map(WynnUtils::getUnformattedString)
                .toList();
    }

    public static String getUnformattedString(String string) {
        return string.replaceAll("\uDAFF\uDFFC\uE006\uDAFF\uDFFF\uE002\uDAFF\uDFFE", "")
                .replaceAll("\uDAFF\uDFFC\uE001\uDB00\uDC06", "")
                .replaceAll("§.", "")
                .replaceAll("&.", "")
                .replaceAll("\\[[0-9:]+]", "")
                .replaceAll("\\s+", " ")
                .replaceAll("\\n", "")
                .replaceAll("[^\\x20-\\x7E]", "")
                .replaceAll("\u00A0", " ")
                .replaceAll("\\[", "")
                .replaceAll("]", "")
                .trim();
    }

    public static boolean isWynncraftServer(String host) {
        return WYNNCRAFT_SERVER_PATTERN.matcher(host).matches();
    }

    public static boolean isWynncraftWorld(String input) {
        return WORLD_NAME_TABLIST_ENTRY.matcher(input).matches();
    }
}
