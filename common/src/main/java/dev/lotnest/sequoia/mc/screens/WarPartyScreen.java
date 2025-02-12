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

    private FlowLayout partyEntries;
    private FlowLayout rightPartyInfo;
    private FlowLayout rightPartyHolder;
    private FlowLayout rightPartyJoin;

    public WarPartyScreen() {
        warParties = Models.WarParty.getActiveWarParties();
        activeWars = Models.War.getActiveWars();
        selectedRole = WarPartyModel.Role.NONE;
    }

    @Override
    protected OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        FlowLayout rightPartyDescription;
        FlowLayout rightPartyActions;
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        FlowLayout box = Containers.horizontalFlow(Sizing.fill(50), Sizing.fill(60));
        FlowLayout leftPanel = Containers.verticalFlow(Sizing.fill(50), Sizing.fill(100));
        FlowLayout rightPanel = Containers.verticalFlow(Sizing.fill(50), Sizing.fill(100));

        box.surface(Surface.OPTIONS_BACKGROUND);
        leftPanel
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(5));
        rightPanel
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(15));

        rightPartyInfo = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(30));
        rightPartyInfo.alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

        rightPartyHolder = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(40));
        rightPartyHolder
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        rightPartyJoin = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(30));
        rightPartyJoin
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM)
                .allowOverflow(true);

        //        rightPartyDescription = Containers.verticalFlow(Sizing.content(), Sizing.fill(100));
        //        rightPartyDescription
        //                .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
        //                .padding(Insets.of(5));
        //
        //        rightPartyActions = Containers.verticalFlow(Sizing.content(), Sizing.fill(100));
        //        rightPartyActions.alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);
        //
        //        //        rightPartyHolder.child(rightPartyDescription);
        //        //        rightPartyHolder.child(rightPartyActions);

        partyEntries = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        partyEntries.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        updateLeftPanel();

        ScrollContainer<FlowLayout> scrollableParties =
                Containers.verticalScroll(Sizing.fill(100), Sizing.fill(80), partyEntries);
        scrollableParties
                .scrollbar(ScrollContainer.Scrollbar.vanilla())
                .scrollbarThiccness(8)
                .scrollStep(10)
                .allowOverflow(false)
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);

        leftPanel.child(scrollableParties);

        updateRightPanel();

        rightPanel.child(rightPartyInfo);
        rightPanel.child(rightPartyHolder);
        rightPanel.child(rightPartyJoin);
        box.child(leftPanel);
        box.child(rightPanel);
        rootComponent.child(box);
    }

    private void updateLeftPanel() {
        partyEntries.clearChildren();
        warParties = Models.WarParty.getActiveWarParties();
        activeWars = Models.War.getActiveWars();

        for (GuildWarParty party : warParties.values()) {
            ButtonComponent partyButton = Components.button(
                    Component.literal(party.territory()).withStyle(ChatFormatting.GREEN), buttonComponent -> {
                        selectedParty = party;
                        selectedWar = null;
                        updateRightPanel();
                    });
            partyButton.sizing(Sizing.fixed(140), Sizing.fixed(30));
            partyButton.margins(Insets.of(3));
            partyButton.tooltip(Component.literal("Click for party info!"));
            partyEntries.child(partyButton);
        }

        for (GuildWar war : activeWars.values()) {
            if (!war.hasParty()) {
                ButtonComponent warButton = Components.button(
                        Component.literal(war.territory()).withStyle(ChatFormatting.RED), buttonComponent -> {
                            selectedWar = war;
                            selectedParty = null;
                            updateRightPanel();
                        });
                warButton.sizing(Sizing.fixed(140), Sizing.fixed(30));
                warButton.margins(Insets.of(3));
                warButton.tooltip(Component.literal("Click for war info!"));
                partyEntries.child(warButton);
            }
        }
    }

    private void updateRightPanel() {
        rightPartyInfo.clearChildren();
        rightPartyHolder.clearChildren();
        rightPartyJoin.clearChildren();

        if (selectedParty == null && selectedWar == null) {
            rightPartyInfo.child(Components.label(
                    Component.literal("Select something to display!").withStyle(ChatFormatting.GRAY)));
            return;
        }

        if (selectedParty == null) {
            updateRightPanelForWar();
        } else {
            updateRightPanelForParty();
        }
    }

    private void updateRightPanelForWar() {
        rightPartyInfo
                .child(Components.label(Component.literal("This war has no party yet!")
                                .withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD))
                        .margins(Insets.bottom(10)))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        addRightPartyInfoChild(
                selectedWar.territory(),
                selectedWar.difficulty().getDisplayName(),
                selectedWar.difficulty().getColor());

        ButtonComponent createButton =
                Components.button(Component.literal("Create Party"), buttonComponent -> createParty());
        createButton.sizing(Sizing.fixed(80), Sizing.fixed(20));
        rightPartyJoin
                .child(createButton.margins(Insets.top(10)))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

    private void updateRightPanelForParty() {
        selectedRole = selectedParty.members().getOrDefault(McUtils.playerName(), WarPartyModel.Role.NONE);

        rightPartyInfo
                .child(Components.label(
                                Component.literal("Party Info").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD))
                        .margins(Insets.bottom(10)))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

        addRightPartyInfoChild(
                selectedParty.territory(),
                selectedParty.difficulty().getDisplayName(),
                selectedParty.difficulty().getColor());

        rightPartyHolder
                .child(Components.label(Component.literal("Members:").withStyle(ChatFormatting.BOLD))
                        .margins(Insets.bottom(2)))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

        FlowLayout membersList = Containers.verticalFlow(Sizing.content(), Sizing.content());
        for (Map.Entry<String, WarPartyModel.Role> entry :
                selectedParty.members().entrySet()) {
            String member = entry.getKey();
            WarPartyModel.Role role = entry.getValue();
            membersList.child(Components.label(Component.literal("  " + member + " ")
                    .withStyle(ChatFormatting.AQUA)
                    .append(Component.literal(role.getDisplayName()).withStyle(ChatFormatting.LIGHT_PURPLE))));
        }
        rightPartyHolder.child(
                membersList.margins(Insets.bottom(10)).alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP));

        if (selectedParty.members().containsKey(McUtils.playerName())) {
            DropdownComponent roleDropdown = Components.dropdown(Sizing.content())
                    .nested(
                            Component.literal(
                                    selectedRole == WarPartyModel.Role.NONE
                                            ? " Please select a role."
                                            : " Selected role: " + selectedRole),
                            Sizing.content(),
                            dropdown -> {
                                dropdown.button(Component.literal("DPS"), d -> updateRole(WarPartyModel.Role.DPS));
                                dropdown.button(
                                        Component.literal("Healer"), d -> updateRole(WarPartyModel.Role.HEALER));
                                dropdown.button(Component.literal("Tank"), d -> updateRole(WarPartyModel.Role.TANK));
                                dropdown.button(Component.literal("Solo"), d -> updateRole(WarPartyModel.Role.SOLO));
                            });
            roleDropdown.surface(Surface.DARK_PANEL).horizontalAlignment(HorizontalAlignment.CENTER);
            rightPartyJoin.child(roleDropdown);
        }

        boolean isMember = selectedParty.members().containsKey(McUtils.playerName());
        ButtonComponent joinButton = Components.button(
                Component.literal(isMember ? "Leave" : "Join"), buttonComponent -> toggleJoinLeave(isMember));
        joinButton.sizing(Sizing.fixed(80), Sizing.fixed(20));
        rightPartyJoin
                .child(joinButton.margins(Insets.top(10)))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);

        if (selectedParty.partyLeader().equals(McUtils.playerName())) {
            ButtonComponent disbandButton =
                    Components.button(Component.literal("Disband"), buttonComponent -> disbandParty());
            disbandButton.sizing(Sizing.fixed(80), Sizing.fixed(20));
            rightPartyHolder
                    .child(disbandButton.margins(Insets.top(10)))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
        }
    }

    private void addRightPartyInfoChild(String territory, String difficultyDisplay, ChatFormatting difficultyColor) {
        rightPartyInfo
                .child(Components.label(Component.literal("Territory: ")
                                .withStyle(ChatFormatting.GRAY)
                                .append(Component.literal(territory)
                                        .withStyle(ChatFormatting.BOLD, ChatFormatting.GRAY)))
                        .margins(Insets.bottom(5)))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        rightPartyInfo
                .child(Components.label(Component.literal("Difficulty: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(difficultyDisplay).withStyle(difficultyColor))))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

    public void updateRole(WarPartyModel.Role role) {
        WynntilsMod.postEvent(new WarPartyUpdateRoleEvent(selectedParty.hash(), McUtils.playerName(), role));

        selectedRole = role;
        sendIC3Message(false, "roleUpdate");

        updateRightPanel();
    }

    private void disbandParty() {
        WynntilsMod.postEvent(new WarPartyDisbandEvent(selectedParty.hash()));
        sendIC3Message(false, "partyDisband");
        selectedParty = null;

        updateLeftPanel();
        updateRightPanel();
    }

    private void createParty() {
        WynntilsMod.postEvent(new WarPartyCreatedEvent(
                selectedWar.hash(),
                McUtils.playerName(),
                selectedWar.territory(),
                WarPartyModel.Role.NONE,
                selectedWar.difficulty()));
        sendIC3Message(false, "partyCreate");
        selectedParty = warParties.get(selectedWar.hash());
        selectedWar = null;

        updateLeftPanel();
        updateRightPanel();
    }

    private void toggleJoinLeave(boolean isJoining) {
        sendIC3Message(isJoining, "partyUpdate");
        WynntilsMod.postEvent(
                new WarPartyUpdateEvent(selectedParty.hash(), McUtils.playerName(), isJoining ? 1 : -1, selectedRole));

        updateRightPanel();
    }

    private void sendIC3Message(boolean payload, String method) {
        String message = null;
        switch (method) {
            case "roleUpdate" -> message =
                    String.format("%d;%s;%s", selectedParty.hash(), McUtils.playerName(), selectedRole);
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
            default -> SequoiaMod.error("Unknown method: " + method);
        }

        if (message == null) {
            return;
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
