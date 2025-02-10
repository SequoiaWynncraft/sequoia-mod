/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.screens;

import dev.lotnest.sequoia.features.war.GuildWarParty;
import dev.lotnest.sequoia.models.war.WarPartyModel;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;

public class WarPartyScreen extends BaseOwoScreen<FlowLayout> {
    private final Map<Integer, GuildWarParty> warParties;
    private GuildWarParty selectedParty;

    public WarPartyScreen() {
        this.warParties = generateMockData();
    }

    @Override
    protected OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, (width, height) -> createRootLayout());
    }

    protected FlowLayout createRootLayout() {
        return (FlowLayout) Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100)) // Compact size
                .gap(15) // Space between sections
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.BOTTOM)
                .surface(Surface.VANILLA_TRANSLUCENT)
                .padding(Insets.of(10));
    }

    @Override
    protected void build(FlowLayout root) {
        // **Left Side: Scrollable Party List**

        FlowLayout partyEntries = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100));

        for (GuildWarParty party : warParties.values()) {
            ButtonComponent button = Components.button(Component.literal(party.territory()), b -> {
                this.selectedParty = party;
                buildRightPanel(root);
            });

            button.sizing(Sizing.fixed(120), Sizing.fixed(25));
            button.margins(Insets.of(3));
            button.tooltip(Component.literal(party.members().isEmpty() ? "No members" : "Active party"));

            // Highlight Green (Has Members) or Red (Empty)
            //            button.color(party.members().isEmpty() ? 0xAAFF5555 : 0xAA55FF55);

            partyEntries.child(button);
        }

        FlowLayout leftPanel = Containers.verticalFlow(Sizing.fixed(140), Sizing.fill(100))
                .child(Containers.verticalScroll(Sizing.fixed(140), Sizing.fill(100), partyEntries));
        root.child(leftPanel);

        // **Right Side: Compact Info Panel**
        FlowLayout rightPanel = (FlowLayout) Containers.verticalFlow(Sizing.fixed(220), Sizing.fill(50))
                .gap(5)
                .padding(Insets.of(5))
                .surface(Surface.PANEL);
        root.child(rightPanel);
        buildRightPanel(root);
    }

    private void buildRightPanel(FlowLayout root) {
        FlowLayout rightPanel = (FlowLayout) root.children().get(1);
        rightPanel.clearChildren();

        if (selectedParty == null) {
            rightPanel.child(Components.label(Component.literal("Select a party")));
            return;
        }

        rightPanel.child(Components.label(Component.literal("Party: " + selectedParty.territory())));
        rightPanel.child(Components.label(Component.literal("Leader: " + selectedParty.partyLeader())));
        rightPanel.child(Components.label(
                Component.literal("Members: " + selectedParty.members().size())));

        ButtonComponent joinLeaveButton = Components.button(
                Component.literal(selectedParty.members().containsKey("playerName") ? "Leave" : "Join"),
                b -> toggleJoinLeave());
        joinLeaveButton.sizing(Sizing.fixed(80), Sizing.fixed(18));
        rightPanel.child(joinLeaveButton);

        FlowLayout roleSelection =
                Containers.horizontalFlow(Sizing.content(), Sizing.content()).gap(3);
        roleSelection.child(createRoleButton("DPS"));
        roleSelection.child(createRoleButton("Healer"));
        roleSelection.child(createRoleButton("Tank"));
        roleSelection.child(createRoleButton("Solo"));
        rightPanel.child(roleSelection);
    }

    private ButtonComponent createRoleButton(String role) {
        return (ButtonComponent) Components.button(Component.literal(role), b -> {
                    System.out.println("Selected role: " + role);
                })
                .sizing(Sizing.fixed(45), Sizing.fixed(45));
    }

    private void toggleJoinLeave() {
        if (selectedParty != null) {
            boolean isMember = selectedParty.members().containsKey("playerName");
            if (isMember) {
                selectedParty.members().remove("playerName");
            } else {
                selectedParty.members().put("playerName", WarPartyModel.Role.DPS);
            }
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

// public class WarPartyScreen extends BaseOwoScreen<FlowLayout> {
//    private final Map<Integer, GuildWarParty> warParties;
//
//    public WarPartyScreen() {
//        super(Component.literal("War Parties"));
//        this.warParties = generateMockData(); // Replace this with real data later
//    }
//
//    @Override
//    protected OwoUIAdapter<FlowLayout> createAdapter() {
//        return OwoUIAdapter.create(this, Containers::verticalFlow);
//    }
//
//    @Override
//    protected void build(FlowLayout rootComponent) {
//        rootComponent
//                .surface(Surface.DARK_PANEL)
//                .horizontalAlignment(HorizontalAlignment.CENTER)
//                .verticalAlignment(VerticalAlignment.CENTER)
//                .padding(Insets.of(5));
//
//        for (GuildWarParty party : warParties.values()) {
//            ItemStack paper = new ItemStack(Items.PAPER);
//            paper.set(DataComponents.CUSTOM_NAME, Component.literal("Leader: " + party.partyLeader()));
//
//            String tooltipText = "Leader: " + party.partyLeader() + "\nTerritory: " + party.territory() + "\nMembers:
// "
//                    + party.members().toString();
//
//            rootComponent.child(Components.item(paper).tooltip(Component.literal(tooltipText)));
//        }
//    }
//

// }
