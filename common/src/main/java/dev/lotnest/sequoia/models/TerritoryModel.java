/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.wynntils.core.components.Handlers;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.MenuEvent;
import com.wynntils.models.territories.TerritoryInfo;
import com.wynntils.services.map.pois.TerritoryPoi;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.wynn.ContainerUtils;
import dev.lotnest.sequoia.core.components.Model;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class TerritoryModel extends Model {
    private static final Pattern TERRITORY_CAPTURED_PATTERN =
            Pattern.compile("^(?<guild>.+?) has taken control of (?<territory>.+?)!$");

    private static final Pattern GUILD_MANAGE_TITLE_PATTERN = Pattern.compile(".+: Manage");
    private static final int GUILD_TERRITORY_MENU_ITEM_SLOT = 14;

    private boolean isTerritoryMenuOpened = false;

    public TerritoryModel() {
        super(List.of());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMenuOpenedPre(MenuEvent.MenuOpenedEvent.Pre event) {
        if (!isTerritoryMenuOpened) {
            return;
        }

        isTerritoryMenuOpened = false;

        StyledText title = StyledText.fromComponent(event.getTitle());
        if (title.matches(GUILD_MANAGE_TITLE_PATTERN)) {
            event.setCanceled(true);

            AbstractContainerMenu container = event.getMenuType().create(event.getContainerId(), McUtils.inventory());
            ContainerUtils.clickOnSlot(GUILD_TERRITORY_MENU_ITEM_SLOT, event.getContainerId(), 0, container.getItems());
        }
    }

    public Pair<String, String> parseTerritoryCapturedMessage(String message) {
        Matcher territoryCapturedMatcher = TERRITORY_CAPTURED_PATTERN.matcher(message);
        if (territoryCapturedMatcher.matches()) {
            return Pair.of(territoryCapturedMatcher.group("guild"), territoryCapturedMatcher.group("territory"));
        }
        return null;
    }

    public TerritoryInfo getTerritoryInfo(String territoryName) {
        TerritoryPoi territoryPoi = Models.Territory.getTerritoryPoiFromAdvancement(territoryName);
        if (territoryPoi != null) {
            return territoryPoi.getTerritoryInfo();
        }
        return null;
    }

    public void openTerritoryMenu() {
        isTerritoryMenuOpened = true;
        Handlers.Command.sendCommandImmediately("guild manage");
    }
}
