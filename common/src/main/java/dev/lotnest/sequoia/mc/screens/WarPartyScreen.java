/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.screens;

import com.google.common.collect.Sets;
import dev.lotnest.sequoia.features.war.GuildWar;
import dev.lotnest.sequoia.features.war.GuildWarParty;
import dev.lotnest.sequoia.models.war.WarPartyModel;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class WarPartyScreen extends BaseOwoScreen<FlowLayout> {
    private final Map<Integer, GuildWarParty> warParties;

    private final Set<GuildWar> activeWars;
    private GuildWarParty selectedParty;

    public WarPartyScreen() {
        this.warParties = generateMockData();
        this.activeWars = Sets.newHashSet();
    }

    @Override
    protected OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        FlowLayout Box = Containers.horizontalFlow(Sizing.fill(50), Sizing.fill(50));
        FlowLayout Left = Containers.verticalFlow(Sizing.fill(50), Sizing.fill(100)); // Ensure it has enough height
        FlowLayout Right = Containers.verticalFlow(Sizing.fill(50), Sizing.fill(100));

        Box.surface(Surface.DARK_PANEL);

        Left.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER).padding(Insets.of(5));

        Right.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER).padding(Insets.of(15));

        // **Scrollable Party List**
        FlowLayout partyEntries = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        partyEntries.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        for (GuildWarParty party : warParties.values()) {
            ButtonComponent button =
                    Components.button(Component.literal(party.territory()).withStyle(ChatFormatting.GREEN), b -> {
                        this.selectedParty = party;
                        updateRightPanel(Right);
                    });
            button.sizing(Sizing.fixed(140), Sizing.fixed(30));
            button.margins(Insets.of(3));
            button.tooltip(Component.literal("Click for party info!"));
            partyEntries.child(button);
        }

        ScrollContainer<FlowLayout> parties =
                Containers.verticalScroll(Sizing.fill(100), Sizing.fill(80), partyEntries);

        parties.scrollbar(ScrollContainer.Scrollbar.vanilla())
                .scrollbarThiccness(5)
                .scrollStep(10)
                .allowOverflow(false)
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        Left.child(parties); // Now scrollable and centered

        // **Right Panel: Initially Empty**
        updateRightPanel(Right);

        Box.child(Left);
        Box.child(Right);
        rootComponent.child(Box);
    }

    /**
     * Updates the right panel with the selected party's info.
     */
    private void updateRightPanel(FlowLayout rightPanel) {
        rightPanel.clearChildren();

        if (selectedParty == null) {
            rightPanel.child(
                    Components.label(Component.literal("Select a party").withStyle(ChatFormatting.GRAY)));
            return;
        }

        rightPanel.child(Components.label(Component.literal("Party: " + selectedParty.territory())
                        .withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD))
                .margins(Insets.bottom(5)));

        rightPanel.child(Components.label(Component.literal("Leader: " + selectedParty.partyLeader())
                        .withStyle(ChatFormatting.YELLOW))
                .margins(Insets.bottom(5)));

        // **Party Members & Roles**
        rightPanel.child(Components.label(Component.literal("Members:").withStyle(ChatFormatting.BOLD))
                .margins(Insets.bottom(2)));

        FlowLayout membersList = Containers.verticalFlow(Sizing.content(), Sizing.content());
        for (Map.Entry<String, WarPartyModel.Role> entry :
                selectedParty.members().entrySet()) {
            String member = entry.getKey();
            String role = entry.getValue().toString();
            membersList.child(Components.label(
                    Component.literal("• " + member + " - " + role).withStyle(ChatFormatting.AQUA)));
        }

        rightPanel.child(membersList.margins(Insets.bottom(10)));

        // **Join/Leave Button**
        ButtonComponent joinLeaveButton = Components.button(
                Component.literal(selectedParty.members().containsKey("playerName") ? "Leave" : "Join"),
                b -> toggleJoinLeave(rightPanel));
        joinLeaveButton.sizing(Sizing.fixed(100), Sizing.fixed(20));
        rightPanel.child(joinLeaveButton.margins(Insets.bottom(10)));

        // **Role Selection (Only One Active at a Time)**
        FlowLayout roleSelection =
                Containers.horizontalFlow(Sizing.content(), Sizing.content()).gap(5);
        roleSelection.child(createRoleButton("DPS", roleSelection));
        roleSelection.child(createRoleButton("Healer", roleSelection));
        roleSelection.child(createRoleButton("Tank", roleSelection));
        roleSelection.child(createRoleButton("Solo", roleSelection));
        rightPanel.child(roleSelection);
    }

    /**
     * Creates a role selection button that disables itself when clicked and enables others.
     */
    private ButtonComponent createRoleButton(String role, FlowLayout rightPanel) {
        ButtonComponent roleButton = Components.button(Component.literal(role), b -> {
            if (selectedParty != null) {
                b.active(false);
                try {
                    WarPartyModel.Role selectedRole = WarPartyModel.Role.valueOf(role.toUpperCase());

                    // Disable the clicked button and enable others
                    for (io.wispforest.owo.ui.core.Component child : rightPanel.children()) {
                        if (child instanceof ButtonComponent button) {
                            boolean isThisButton =
                                    button.getMessage().getString().equals(role);
                            boolean isRoleButton = Set.of("DPS", "Healer", "Tank", "Solo")
                                    .contains(button.getMessage().getString());
                            if (!isThisButton && isRoleButton) button.active = true;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid role: " + role);
                }
            }
        });

        roleButton.sizing(Sizing.fixed(50), Sizing.fixed(30));
        return roleButton;
    }

    /**
     * Toggles joining or leaving a party.
     */
    private void toggleJoinLeave(FlowLayout rightPanel) {
        if (selectedParty != null) {
            boolean isMember = selectedParty.members().containsKey("playerName");
            if (isMember) {
                selectedParty.members().remove("playerName");
            } else {
                selectedParty.members().put("playerName", WarPartyModel.Role.DPS);
            }
        }
        updateRightPanel(rightPanel);
    }

    private static Map<Integer, GuildWarParty> generateMockData() {
        Map<Integer, GuildWarParty> parties = new HashMap<>();
        parties.put(1, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(2, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(3, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(4, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));

        parties.put(5, new GuildWarParty(456, "Alex", "Jungle Base", Map.of("Steve", WarPartyModel.Role.HEALER)));
        parties.put(
                6,
                new GuildWarParty(
                        666,
                        "MaidKeeper",
                        "Your Mom",
                        Map.of("MaidKeeper", WarPartyModel.Role.SOLO, "The maid being kept", WarPartyModel.Role.SOLO)));
        parties.put(7, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));

        parties.put(8, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));

        parties.put(9, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));

        parties.put(10, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(11, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(12, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(13, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(14, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(15, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(16, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));
        parties.put(18, new GuildWarParty(123, "Steve", "Desert Outpost", Map.of("Alex", WarPartyModel.Role.DPS)));

        return parties;
    }
}
//
//    @Override
//    protected OwoUIAdapter<FlowLayout> createAdapter() {
//        return OwoUIAdapter.create(this, (width, height) -> createRootLayout());
//    }
//
//    protected FlowLayout createRootLayout() {
//        return (FlowLayout) Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100)) // Compact size
//                .horizontalAlignment(HorizontalAlignment.CENTER)
//                .verticalAlignment(VerticalAlignment.CENTER)
//                .surface(Surface.VANILLA_TRANSLUCENT);
//    }
//
//    @Override
//    protected void build(FlowLayout root) {
//        // **Left Side: Scrollable Party List**
//        FlowLayout Box =
//                Containers.verticalFlow(Sizing.fill(50), Sizing.fill(50));
//        FlowLayout partyEntries = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100));
//
//        for (GuildWarParty party : warParties.values()) {
//            ButtonComponent button = Components.button(Component.literal(party.territory()), b -> {
//                this.selectedParty = party;
//                buildRightPanel(Box);
//            });
//
//            button.sizing(Sizing.fixed(120), Sizing.fixed(25));
//            button.margins(Insets.of(3));
//            button.tooltip(Component.literal(party.members().isEmpty() ? "No members" : "Active party"));
//
//            // Highlight Green (Has Members) or Red (Empty)
//            //            button.color(party.members().isEmpty() ? 0xAAFF5555 : 0xAA55FF55);
//
//            partyEntries.child(button);
//        }
//
//        FlowLayout leftPanel = Containers.verticalFlow(Sizing.fixed(140), Sizing.fill(100))
//                .child(Containers.verticalScroll(Sizing.fixed(140), Sizing.fill(100), partyEntries));
//
//        Box.child(leftPanel);
//
//        // **Right Side: Compact Info Panel**
//        FlowLayout rightPanel = (FlowLayout) Containers.verticalFlow(Sizing.fixed(220), Sizing.fill(50))
//                .gap(5)
//                .padding(Insets.of(5))
//                .surface(Surface.PANEL)
//                .verticalAlignment(VerticalAlignment.CENTER);
//
//        Box.child(rightPanel);
//
//        DraggableContainer<FlowLayout> BoxDrag =
//                Containers.draggable(Sizing.fill(50), Sizing.fill(50), Box).foreheadSize(10);
//        BoxDrag.surface(Surface.DARK_PANEL)
//                .padding(Insets.of(10))
//                .verticalAlignment(VerticalAlignment.CENTER)
//                .horizontalAlignment(HorizontalAlignment.CENTER);
//        root.child(BoxDrag);
//    }
//
//    private void buildRightPanel(FlowLayout root) {
//        FlowLayout rightPanel = (FlowLayout) root.children().get(1);
//        rightPanel.clearChildren();
//
//        if (selectedParty == null) {
//            rightPanel.child(Components.label(Component.literal("Select a party")));
//            return;
//        }
//
//        rightPanel.child(Components.label(Component.literal("Party: " + selectedParty.territory())));
//        rightPanel.child(Components.label(Component.literal("Leader: " + selectedParty.partyLeader())));
//        rightPanel.child(Components.label(
//                Component.literal("Members: " + selectedParty.members().size())));
//
//        ButtonComponent joinLeaveButton = Components.button(
//                Component.literal(selectedParty.members().containsKey("playerName") ? "Leave" : "Join"),
//                b -> toggleJoinLeave());
//        joinLeaveButton.sizing(Sizing.fixed(80), Sizing.fixed(18));
//        rightPanel.child(joinLeaveButton);
//
//        FlowLayout roleSelection =
//                Containers.horizontalFlow(Sizing.content(), Sizing.content()).gap(3);
//        roleSelection.child(createRoleButton("DPS"));
//        roleSelection.child(createRoleButton("Healer"));
//        roleSelection.child(createRoleButton("Tank"));
//        roleSelection.child(createRoleButton("Solo"));
//        rightPanel.child(roleSelection);
//    }
//
//    private ButtonComponent createRoleButton(String role) {
//        return (ButtonComponent) Components.button(Component.literal(role), b -> {
//                    System.out.println("Selected role: " + role);
//                })
//                .sizing(Sizing.fixed(45), Sizing.fixed(45));
//    }
//
//    private void toggleJoinLeave() {
//        if (selectedParty != null) {
//            boolean isMember = selectedParty.members().containsKey("playerName");
//            if (isMember) {
//                selectedParty.members().remove("playerName");
//            } else {
//                selectedParty.members().put("playerName", WarPartyModel.Role.DPS);
//            }
//        }
//    }
