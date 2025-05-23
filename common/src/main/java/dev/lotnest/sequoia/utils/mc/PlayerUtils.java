/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.utils.mc;

import com.google.common.collect.Lists;
import com.wynntils.utils.mc.McUtils;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Scoreboard;

public final class PlayerUtils {
    private PlayerUtils() {}

    public static void sendTitle(Component title) {
        sendTitle(title, null);
    }

    public static void sendTitle(Component title, Component subtitle) {
        sendTitle(title, subtitle, 10, 50, 10);
    }

    public static void sendTitle(
            Component title, Component subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if (title != null) {
            sendTitleText(title);
        }

        if (subtitle != null) {
            sendSubtitleText(subtitle);
        }

        sendTitlesAnimation(fadeInTicks, stayTicks, fadeOutTicks);
    }

    public void sendActionBarText(Component actionBarTextComponent) {
        sendActionBarText(actionBarTextComponent, false);
    }

    public static void sendActionBarText(Component actionBarTextComponent, boolean animateColor) {
        Minecraft.getInstance().gui.setOverlayMessage(actionBarTextComponent, animateColor);
    }

    public static void sendTitleText(Component titleComponent) {
        Minecraft.getInstance().gui.setTitle(titleComponent);
    }

    public static void sendSubtitleText(Component subtitleComponent) {
        Minecraft.getInstance().gui.setSubtitle(subtitleComponent);
    }

    public static void sendTitlesAnimation(int titleFadeInTime, int titleStayTime, int titleFadeOutTime) {
        Minecraft.getInstance().gui.setTimes(titleFadeInTime, titleStayTime, titleFadeOutTime);
    }

    public static List<String> getScoreboardLines() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Scoreboard scoreboard = player.level().getScoreboard();
            Objective sidebarObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

            if (sidebarObjective != null) {
                List<String> displayedLines = Lists.newArrayList();

                for (PlayerScoreEntry entry : scoreboard.listPlayerScores(sidebarObjective)) {
                    if (!entry.isHidden()) {
                        Component scoreboardLineComponent = entry.ownerName();
                        if (!scoreboardLineComponent.getString().matches("À+")) {
                            displayedLines.add(scoreboardLineComponent.getString());
                        }
                    }
                }

                return displayedLines;
            }
        }

        return Collections.emptyList();
    }

    public static boolean isSelf(Player player) {
        return McUtils.player() == player;
    }

    public static void swapOffhand() {
        KeyMapping.click(Minecraft.getInstance().options.keySwapOffhand.key);
    }
}
