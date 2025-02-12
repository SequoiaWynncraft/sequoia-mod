/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.mc.screens;

import com.wynntils.core.WynntilsMod;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Models;
import dev.lotnest.sequoia.core.events.WarPartyCreatedEvent;
import dev.lotnest.sequoia.core.events.WarPartyDisbandEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateEvent;
import dev.lotnest.sequoia.core.events.WarPartyUpdateRoleEvent;
import dev.lotnest.sequoia.core.websocket.messages.ic3.GIC3HWSMessage;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class WarPartyScreen extends BaseOwoScreen<FlowLayout> {
    private Map<Integer, GuildWarParty> warParties;

    private Map<Integer, GuildWar> activeWars;
    private GuildWarParty selectedParty;
    private GuildWar selectedWar;
    private WarPartyModel.Role selectedRole;

    public WarPartyScreen() {
        this.warParties = Models.WarParty.getActiveWarParties();
        this.activeWars = Models.War.getActiveWars();
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

        updateLeftSide(partyEntries, RightPartyInfo, RightPartyDescription, RightPartyJoin);

        ScrollContainer<FlowLayout> parties =
                Containers.verticalScroll(Sizing.fill(100), Sizing.fill(80), partyEntries);

        parties.scrollbar(ScrollContainer.Scrollbar.vanilla())
                .scrollbarThiccness(8)
                .scrollStep(10)
                .allowOverflow(false)
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        Left.child(parties);

        // **Right Panel: Initially Empty**
        updateRightPanel(RightPartyInfo, RightPartyHolder, RightPartyJoin, partyEntries, RightPartyDescription);

        RightPartyHolder.child(RightPartyDescription);
        RightPartyHolder.child(RightPartyActions);
        Right.child(RightPartyInfo);
        Right.child(RightPartyHolder);
        Right.child(RightPartyJoin);
        Box.child(Left);
        Box.child(Right);
        rootComponent.child(Box);
    }

    private void updateLeftSide(
            FlowLayout partyEntries,
            FlowLayout RightPartyInfo,
            FlowLayout RightPartyDescription,
            FlowLayout RightPartyJoin) {
        partyEntries.clearChildren();
        this.warParties = Models.WarParty.getActiveWarParties();
        this.activeWars = Models.War.getActiveWars();
        for (GuildWarParty party : warParties.values()) {
            ButtonComponent button =
                    Components.button(Component.literal(party.territory()).withStyle(ChatFormatting.GREEN), b -> {
                        this.selectedParty = party;
                        this.selectedWar = null;
                        updateRightPanel(
                                RightPartyInfo,
                                RightPartyDescription,
                                RightPartyJoin,
                                partyEntries,
                                RightPartyDescription);
                    });
            button.sizing(Sizing.fixed(140), Sizing.fixed(30));
            button.margins(Insets.of(3));
            button.tooltip(Component.literal("Click for party info!"));
            partyEntries.child(button);
        }
        for (GuildWar war : activeWars.values()) {
            if (!war.hasParty()) {
                ButtonComponent button =
                        Components.button(Component.literal(war.territory()).withStyle(ChatFormatting.RED), b -> {
                            this.selectedWar = war;
                            this.selectedParty = null;
                            updateRightPanel(
                                    RightPartyInfo,
                                    RightPartyDescription,
                                    RightPartyJoin,
                                    partyEntries,
                                    RightPartyDescription);
                        });
                button.sizing(Sizing.fixed(140), Sizing.fixed(30));
                button.margins(Insets.of(3));
                button.tooltip(Component.literal("Click for war info!"));
                partyEntries.child(button);
            }
        }
    }
    /**
     * Updates the right panel with the selected party's info.
     */
    private void updateRightPanel(
            FlowLayout RightPartyInfo,
            FlowLayout RightPartyHolder,
            FlowLayout RightPartyJoin,
            FlowLayout partyEntries,
            FlowLayout RightPartyDescription) {
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
                    b -> createParty(
                            RightPartyInfo,
                            RightPartyDescription,
                            RightPartyJoin,
                            partyEntries,
                            RightPartyDescription));
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

//            // **Leader Info**
//            RightPartyHolder.child(Components.label(Component.literal("Leader: " + selectedParty.partyLeader())
//                                    .withStyle(ChatFormatting.YELLOW))
//                            .margins(Insets.bottom(5)))
//                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

            // **Party Members**
            RightPartyHolder.child(
                    Components.label(Component.literal("Members:").withStyle(ChatFormatting.BOLD))
                            .margins(Insets.bottom(2))).alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

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
                                                    RightPartyJoin,
                                                    partyEntries,
                                                    RightPartyDescription));
                                    dropdownComponent.button(
                                            Component.literal("Healer"),
                                            d -> updateRole(
                                                    WarPartyModel.Role.HEALER,
                                                    RightPartyInfo,
                                                    RightPartyHolder,
                                                    RightPartyJoin,
                                                    partyEntries,
                                                    RightPartyDescription));
                                    dropdownComponent.button(
                                            Component.literal("Tank"),
                                            d -> updateRole(
                                                    WarPartyModel.Role.TANK,
                                                    RightPartyInfo,
                                                    RightPartyHolder,
                                                    RightPartyJoin,
                                                    partyEntries,
                                                    RightPartyDescription));
                                    dropdownComponent.button(
                                            Component.literal("Solo"),
                                            d -> updateRole(
                                                    WarPartyModel.Role.SOLO,
                                                    RightPartyInfo,
                                                    RightPartyHolder,
                                                    RightPartyJoin,
                                                    partyEntries,
                                                    RightPartyDescription));
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
                            RightPartyJoin,
                            partyEntries,
                            RightPartyDescription));
            joinButton.sizing(Sizing.fixed(80), Sizing.fixed(20));
            RightPartyJoin.child(joinButton.margins(Insets.top(10)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
            if (this.selectedParty.partyLeader().matches(McUtils.playerName())) {
                ButtonComponent disbandButton = Components.button(
                        Component.literal("Disband"),
                        b -> disbandParty(
                                RightPartyInfo,
                                RightPartyDescription,
                                RightPartyJoin,
                                partyEntries,
                                RightPartyDescription));
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
            FlowLayout RightPartyJoin,
            FlowLayout partyEntries,
            FlowLayout RightPartyDescription) {
        WynntilsMod.postEvent(new WarPartyUpdateRoleEvent(selectedParty.hash(), McUtils.playerName(), role));
        this.selectedRole = role;

        sendMessageToServer(false, "roleUpdate");
        // TODO send message to IC3

        updateRightPanel(RightPartyInfo, RightPartyDescription, RightPartyJoin, partyEntries, RightPartyDescription);
    }

    private void disbandParty(
            FlowLayout RightPartyInfo,
            FlowLayout RightPartyHolder,
            FlowLayout RightPartyJoin,
            FlowLayout partyEntries,
            FlowLayout RightPartyDescription) {
        WynntilsMod.postEvent(new WarPartyDisbandEvent(selectedParty.hash()));
        // TODO send message to IC3
        sendMessageToServer(false, "partyDisband");
        this.selectedParty = null;
        updateLeftSide(partyEntries, RightPartyInfo, RightPartyDescription, RightPartyJoin);
        updateRightPanel(RightPartyInfo, RightPartyDescription, RightPartyJoin, partyEntries, RightPartyDescription);
    }

    private void createParty(
            FlowLayout RightPartyInfo,
            FlowLayout RightPartyHolder,
            FlowLayout RightPartyJoin,
            FlowLayout partyEntries,
            FlowLayout RightPartyDescription) {
        WynntilsMod.postEvent(new WarPartyCreatedEvent(
                selectedWar.hash(),
                McUtils.playerName(),
                selectedWar.territory(),
                WarPartyModel.Role.NONE,
                selectedWar.difficulty()));
        // TODO send message to IC3
        sendMessageToServer(false, "partyCreate");
        this.selectedParty = warParties.get(selectedWar.hash());
        this.selectedWar = null;
        updateLeftSide(partyEntries, RightPartyInfo, RightPartyDescription, RightPartyJoin);
        updateRightPanel(RightPartyInfo, RightPartyDescription, RightPartyJoin, partyEntries, RightPartyDescription);
    }

    private void toggleJoinLeave(
            boolean join,
            FlowLayout RightPartyInfo,
            FlowLayout RightPartyHolder,
            FlowLayout RightPartyJoin,
            FlowLayout partyEntries,
            FlowLayout RightPartyDescription) {
        // TODO send message to IC3
        sendMessageToServer(join, "partyUpdate");
        WynntilsMod.postEvent(
                new WarPartyUpdateEvent(selectedParty.hash(), McUtils.playerName(), join ? 1 : -1, selectedRole));
        updateRightPanel(RightPartyInfo, RightPartyDescription, RightPartyJoin, partyEntries, RightPartyDescription);
    }

    private void sendMessageToServer(boolean payload, String method) {
        String message = null;
        switch (method) {
            case "roleUpdate" -> message =
                    String.format("%d;%s;%s", selectedParty.hash(), McUtils.playerName(), this.selectedRole);
            case "partyCreate" -> message = String.format(
                    "%d;%s;%s;%s;%s",
                    selectedWar.hash(),
                    McUtils.playerName(),
                    selectedWar.territory(),
                    WarPartyModel.Role.NONE,
                    selectedWar.difficulty().getDisplayName());
            case "partyDisband" -> message = String.valueOf(selectedParty.hash());
            case "partyUpdate" -> message =
                    String.format("%d;%d;%s", selectedParty.hash(), payload ? 1 : -1, selectedRole);
        }

        if (!SequoiaMod.getWebSocketFeature().getClient().isOpen()) {
            SequoiaMod.getWebSocketFeature().connectIfNeeded();
        }
        String[] target = new String[] {"*"};
        GIC3HWSMessage.Data data = new GIC3HWSMessage.Data(1, 0, method, message.getBytes(), target);
        GIC3HWSMessage gic3HWSMessage = new GIC3HWSMessage(data);
        SequoiaMod.getWebSocketFeature().sendAsJson(gic3HWSMessage);
    }
}
