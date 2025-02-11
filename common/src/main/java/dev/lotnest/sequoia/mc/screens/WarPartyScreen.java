/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.screens;

import com.wynntils.core.WynntilsMod;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.core.events.WarPartyCreatedEvent;
import dev.lotnest.sequoia.core.events.WarPartyDisbandEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateRoleEvent;
import dev.lotnest.sequoia.features.war.GuildWar;
import dev.lotnest.sequoia.features.war.GuildWarParty;
import dev.lotnest.sequoia.models.war.WarPartyModel;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.DropdownComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.Map;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class WarPartyScreen extends BaseOwoScreen<FlowLayout> {
    private final Map<Integer, GuildWarParty> warParties;

    private final Set<GuildWar> activeWars;
    private GuildWarParty selectedParty;
    private GuildWar selectedWar;
    private WarPartyModel.Role selectedRole;

    public WarPartyScreen(Map<Integer, GuildWarParty> warParties, Set<GuildWar> activeWars) {
        this.warParties = warParties;
        this.activeWars = activeWars;
        this.selectedRole = WarPartyModel.Role.NONE;
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

        FlowLayout Box = Containers.horizontalFlow(Sizing.fill(50), Sizing.fill(60));
        FlowLayout Left = Containers.verticalFlow(Sizing.fill(50), Sizing.fill(100));
        FlowLayout Right = Containers.verticalFlow(Sizing.fill(50), Sizing.fill(100));

        Box.surface(Surface.OPTIONS_BACKGROUND);

        Left.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER).padding(Insets.of(5));

        Right.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER).padding(Insets.of(15));

        FlowLayout RightPartyInfo = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(30));
        RightPartyInfo.alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

        FlowLayout RightPartyHolder = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(40));
        RightPartyHolder.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        FlowLayout RightPartyJoin = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(30));
        RightPartyJoin.alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM)
                .allowOverflow(true);

        FlowLayout RightPartyDescription = Containers.verticalFlow(Sizing.content(), Sizing.fill(100));
        FlowLayout RightPartyActions = Containers.verticalFlow(Sizing.content(), Sizing.fill(100));

        RightPartyDescription.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                .padding(Insets.of(5));
        RightPartyActions.alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        // **Scrollable Party List**
        FlowLayout partyEntries = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        partyEntries.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        for (GuildWarParty party : warParties.values()) {
            ButtonComponent button =
                    Components.button(Component.literal(party.territory()).withStyle(ChatFormatting.GREEN), b -> {
                        this.selectedParty = party;
                        this.selectedWar = null;
                        updateRightPanel(RightPartyInfo, RightPartyDescription, RightPartyJoin);
                    });
            button.sizing(Sizing.fixed(140), Sizing.fixed(30));
            button.margins(Insets.of(3));
            button.tooltip(Component.literal("Click for party info!"));
            partyEntries.child(button);
        }
        for (GuildWar war : activeWars) {
            ButtonComponent button =
                    Components.button(Component.literal(war.territory()).withStyle(ChatFormatting.RED), b -> {
                        this.selectedWar = war;
                        this.selectedParty = null;
                        updateRightPanel(RightPartyInfo, RightPartyDescription, RightPartyJoin);
                    });
            button.sizing(Sizing.fixed(140), Sizing.fixed(30));
            button.margins(Insets.of(3));
            button.tooltip(Component.literal("Click for war info!"));
            partyEntries.child(button);
        }

        ScrollContainer<FlowLayout> parties =
                Containers.verticalScroll(Sizing.fill(100), Sizing.fill(80), partyEntries);

        parties.scrollbar(ScrollContainer.Scrollbar.vanilla())
                .scrollbarThiccness(8)
                .scrollStep(10)
                .allowOverflow(false)
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        Left.child(parties);

        // **Right Panel: Initially Empty**
        updateRightPanel(RightPartyInfo, RightPartyHolder, RightPartyJoin);

        RightPartyHolder.child(RightPartyDescription);
        RightPartyHolder.child(RightPartyActions);
        Right.child(RightPartyInfo);
        Right.child(RightPartyHolder);
        Right.child(RightPartyJoin);
        Box.child(Left);
        Box.child(Right);
        rootComponent.child(Box);
    }

    /**
     * Updates the right panel with the selected party's info.
     */
    private void updateRightPanel(FlowLayout RightPartyInfo, FlowLayout RightPartyHolder, FlowLayout RightPartyJoin) {
        RightPartyInfo.clearChildren();
        RightPartyHolder.clearChildren();
        RightPartyJoin.clearChildren();

        if (selectedParty == null && selectedWar == null) {
            RightPartyInfo.child(Components.label(
                    Component.literal("Select something to display!").withStyle(ChatFormatting.GRAY)));
            return;
        }

        if (selectedParty == null) {
            RightPartyInfo.child(Components.label(Component.literal("This war has no party yet!")
                                    .withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD))
                            .margins(Insets.bottom(10)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
            RightPartyInfo.child(Components.label(Component.literal("Territory: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(selectedWar.territory())
                                            .withStyle(ChatFormatting.BOLD, ChatFormatting.GRAY)))
                            .margins(Insets.bottom(5)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
            RightPartyInfo.child(Components.label(Component.literal("Difficulty: ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(selectedWar.difficulty().getDisplayName())
                                    .withStyle(selectedWar.difficulty().getColor()))))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

            ButtonComponent createButton = Components.button(
                    Component.literal("Create Party"),
                    b -> createParty(RightPartyInfo, RightPartyHolder, RightPartyJoin));
            createButton.sizing(Sizing.fixed(80), Sizing.fixed(20));
            RightPartyJoin.child(createButton.margins(Insets.top(10)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        }

        if (selectedWar == null) {
            selectedRole = this.selectedParty.members().getOrDefault(McUtils.playerName(), WarPartyModel.Role.NONE);
            // **Title**
            RightPartyInfo.child(Components.label(
                                    Component.literal("Party Info").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD))
                            .margins(Insets.bottom(10)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
            RightPartyInfo.child(Components.label(Component.literal("Territory: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(selectedParty.territory())
                                            .withStyle(ChatFormatting.BOLD, ChatFormatting.GRAY)))
                            .margins(Insets.bottom(5)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
            RightPartyInfo.child(Components.label(Component.literal("Difficulty: ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(selectedParty.difficulty().getDisplayName())
                                    .withStyle(selectedParty.difficulty().getColor()))))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

            // **Leader Info**
            RightPartyHolder.child(Components.label(Component.literal("Leader: " + selectedParty.partyLeader())
                                    .withStyle(ChatFormatting.YELLOW))
                            .margins(Insets.bottom(5)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

            // **Party Members**
            RightPartyHolder.child(
                    Components.label(Component.literal("Members:").withStyle(ChatFormatting.BOLD))
                            .margins(Insets.bottom(2)));

            FlowLayout membersList = Containers.verticalFlow(Sizing.content(), Sizing.content());
            for (Map.Entry<String, WarPartyModel.Role> entry :
                    selectedParty.members().entrySet()) {
                String member = entry.getKey();
                WarPartyModel.Role role = entry.getValue();
                membersList.child(Components.label(Component.literal("  " + member + " ")
                        .withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(role.getDisplayName()).withStyle(ChatFormatting.LIGHT_PURPLE))));
            }
            RightPartyHolder.child(membersList
                    .margins(Insets.bottom(10))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER));
            if (selectedParty.members().containsKey(McUtils.playerName())) {
                DropdownComponent roleDropdown = Components.dropdown(Sizing.content())
                        .nested(
                                Component.literal(
                                        this.selectedRole == WarPartyModel.Role.NONE
                                                ? " Please select a role."
                                                : " Selected role: " + selectedRole),
                                Sizing.content(),
                                dropdownComponent -> {
                                    dropdownComponent.button(
                                            Component.literal("DPS"),
                                            d -> updateRole(
                                                    WarPartyModel.Role.DPS,
                                                    RightPartyInfo,
                                                    RightPartyHolder,
                                                    RightPartyJoin));
                                    dropdownComponent.button(
                                            Component.literal("Healer"),
                                            d -> updateRole(
                                                    WarPartyModel.Role.HEALER,
                                                    RightPartyInfo,
                                                    RightPartyHolder,
                                                    RightPartyJoin));
                                    dropdownComponent.button(
                                            Component.literal("Tank"),
                                            d -> updateRole(
                                                    WarPartyModel.Role.TANK,
                                                    RightPartyInfo,
                                                    RightPartyHolder,
                                                    RightPartyJoin));
                                    dropdownComponent.button(
                                            Component.literal("Solo"),
                                            d -> updateRole(
                                                    WarPartyModel.Role.SOLO,
                                                    RightPartyInfo,
                                                    RightPartyHolder,
                                                    RightPartyJoin));
                                });

                roleDropdown.surface(Surface.DARK_PANEL).horizontalAlignment(HorizontalAlignment.CENTER);
                RightPartyJoin.child(roleDropdown);
            }

            // **Join Button**
            ButtonComponent joinButton = Components.button(
                    Component.literal(selectedParty.members().containsKey(McUtils.playerName()) ? "Leave" : "Join"),
                    b -> toggleJoinLeave(
                            selectedParty.members().containsKey(McUtils.playerName()),
                            RightPartyInfo,
                            RightPartyHolder,
                            RightPartyJoin));
            joinButton.sizing(Sizing.fixed(80), Sizing.fixed(20));
            RightPartyJoin.child(joinButton.margins(Insets.top(10)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
            if (this.selectedParty.partyLeader() == McUtils.playerName()) {
                ButtonComponent disbandButton = Components.button(
                        Component.literal("Disband"),
                        b -> disbandParty(RightPartyInfo, RightPartyHolder, RightPartyJoin));
                disbandButton.sizing(Sizing.fixed(80), Sizing.fixed(20));
                RightPartyHolder.child(disbandButton.margins(Insets.top(10)))
                        .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
            }
        }
    }

    public void updateRole(
            WarPartyModel.Role role,
            FlowLayout RightPartyInfo,
            FlowLayout RightPartyHolder,
            FlowLayout RightPartyJoin) {
        WynntilsMod.postEvent(new WarPartyUpdateRoleEvent(selectedParty.hash(), McUtils.playerName(), role));
        // TODO send message to IC3

        updateRightPanel(RightPartyInfo, RightPartyHolder, RightPartyJoin);
    }

    private void disbandParty(FlowLayout RightPartyInfo, FlowLayout RightPartyHolder, FlowLayout RightPartyJoin) {
        WynntilsMod.postEvent(new WarPartyDisbandEvent(selectedParty.hash()));
        // TODO send message to IC3
        this.selectedParty = null;
        updateRightPanel(RightPartyInfo, RightPartyHolder, RightPartyJoin);
    }

    private void createParty(FlowLayout RightPartyInfo, FlowLayout RightPartyHolder, FlowLayout RightPartyJoin) {
        WynntilsMod.postEvent(new WarPartyCreatedEvent(
                selectedWar.hash(),
                McUtils.playerName(),
                selectedWar.territory(),
                WarPartyModel.Role.NONE,
                selectedWar.difficulty()));
        // TODO send message to IC3
        this.selectedParty = warParties.get(selectedWar.hash());
        this.selectedWar = null;
        updateRightPanel(RightPartyInfo, RightPartyHolder, RightPartyJoin);
    }

    private void toggleJoinLeave(
            boolean join, FlowLayout RightPartyInfo, FlowLayout RightPartyHolder, FlowLayout RightPartyJoin) {
        // TODO send message to IC3
        WynntilsMod.postEvent(
                new WarPartyUpdateEvent(selectedParty.hash(), McUtils.playerName(), join ? 1 : -1, selectedRole));
        updateRightPanel(RightPartyInfo, RightPartyHolder, RightPartyJoin);
    }
}
