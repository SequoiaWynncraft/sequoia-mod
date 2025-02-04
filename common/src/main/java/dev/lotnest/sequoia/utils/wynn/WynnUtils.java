/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.utils.wynn;

import static com.wynntils.models.character.CharacterModel.GUILD_MENU_SLOT;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.container.scriptedquery.QueryBuilder;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.buffered.CustomRenderType;
import com.wynntils.utils.wynn.InventoryUtils;
import dev.lotnest.sequoia.mc.MinecraftUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;

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

        return MinecraftUtils.isValidUsername(name);
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

    public static boolean isSequoiaGuildMember() {
        QueryBuilder queryBuilder = ScriptedContainerQuery.builder("Character Info Query");
        queryBuilder.onError(msg -> WynntilsMod.warn("Error querying Character Info: " + msg));
        queryBuilder.then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM)
                .expectContainerTitle(ContainerModel.CHARACTER_INFO_NAME)
                .processIncomingContainer(WynnUtils::parseCharacterContainerForGuildInfo));

        Models.Guild.addGuildContainerQuerySteps(queryBuilder);

        queryBuilder.build().executeQuery();

        return StringUtils.equals(Models.Guild.getGuildName(), "Sequoia");
    }

    public static void parseCharacterContainerForGuildInfo(ContainerContent container) {
        ItemStack guildInfoItem = container.items().get(GUILD_MENU_SLOT);
        Models.Guild.parseGuildInfoFromGuildMenu(guildInfoItem);
    }

    /**
     * Renders a circle with the given radius. Some notes for future reference:<p>
     * - The circle is rendered at the player's feet, from the ground to HEIGHT blocks above the ground.<p>
     * - .color() takes floats from 0-1, but ints from 0-255<p>
     * - Increase SEGMENTS to make the circle smoother, but it will also increase the amount of vertices (and thus the amount of memory used and the amount of time it takes to render)<p>
     * - The order of the consumer.vertex() calls matter. Here, we draw a quad, so we do bottom left corner, top left corner, top right corner, bottom right corner. This is filled in with the color we set.<p>
     *
     * @param poseStack The pose stack to render with. This is supposed to be the pose stack from the event.
     *                  We do the translation here, so no need to do it before passing it in.
     * @param radius
     * @param color
     */
    public static void renderCircle(
            MultiBufferSource.BufferSource BUFFER_SOURCE,
            int CIRCLE_SEGMENTS,
            float CIRCLE_HEIGHT,
            PoseStack poseStack,
            Position position,
            float radius,
            int color) {
        // Circle must be rendered on both sides, otherwise it will be invisible when looking at
        // it from the outside
        RenderSystem.disableCull();

        poseStack.pushPose();
        poseStack.translate(-position.x(), -position.y(), -position.z());
        VertexConsumer consumer = BUFFER_SOURCE.getBuffer(CustomRenderType.POSITION_COLOR_QUAD);

        Matrix4f matrix4f = poseStack.last().pose();
        double angleStep = 2 * Math.PI / CIRCLE_SEGMENTS;
        double startingAngle = -(System.currentTimeMillis() % 40000) * 2 * Math.PI / 40000.0;
        double angle = startingAngle;
        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            float x = (float) (position.x() + Math.sin(angle) * radius);
            float z = (float) (position.z() + Math.cos(angle) * radius);
            consumer.addVertex(matrix4f, x, (float) position.y(), z).setColor(color);
            consumer.addVertex(matrix4f, x, (float) position.y() + CIRCLE_HEIGHT, z)
                    .setColor(color);
            angle += angleStep;
            float x2 = (float) (position.x() + Math.sin(angle) * radius);
            float z2 = (float) (position.z() + Math.cos(angle) * radius);
            consumer.addVertex(matrix4f, x2, (float) position.y() + CIRCLE_HEIGHT, z2)
                    .setColor(color);
            consumer.addVertex(matrix4f, x2, (float) position.y(), z2).setColor(color);
        }

        BUFFER_SOURCE.endBatch();
        poseStack.popPose();
        RenderSystem.enableCull();
    }
}
