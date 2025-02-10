/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.screens;

import dev.lotnest.sequoia.features.war.GuildWarParty;
import dev.lotnest.sequoia.models.war.WarPartyModel;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WarPartyScreen extends BaseOwoScreen<FlowLayout> {
    private final Map<Integer, GuildWarParty> warParties;

    public WarPartyScreen() {
        super(Component.literal("War Parties"));
        this.warParties = generateMockData(); // Replace this with real data later
    }

    @Override
    protected OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.DARK_PANEL)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER)
                .padding(Insets.of(5));

        for (GuildWarParty party : warParties.values()) {
            ItemStack paper = new ItemStack(Items.PAPER);
            paper.set(DataComponents.CUSTOM_NAME, Component.literal("Leader: " + party.partyLeader()));

            String tooltipText = "Leader: " + party.partyLeader() + "\nTerritory: " + party.territory() + "\nMembers: "
                    + party.members().toString();

            rootComponent.child(Components.item(paper).tooltip(Component.literal(tooltipText)));
        }
    }

    private static Map<Integer, GuildWarParty> generateMockData() {
        Map<Integer, GuildWarParty> parties = new HashMap<>();
        parties.put(1, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(2, new GuildWarParty(456, "Alex", "Jungle Base", Map.of("Steve", WarPartyModel.Role.HEALER)));
        parties.put(
                3,
                new GuildWarParty(
                        666,
                        "MaidKeeper",
                        "Your Mom",
                        Map.of("MaidKeeper", WarPartyModel.Role.SOLO, "The maid being kept", WarPartyModel.Role.SOLO)));
        return parties;
    }
}
