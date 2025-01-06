package dev.lotnest.sequoia.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class PlayerUtils {
    private PlayerUtils() {}

    public static void sendTitle(Component title) {
        sendTitle(title, null);
    }

    public static void sendTitle(Component title, Component subTitle) {
        sendTitle(title, subTitle, 10, 50, 10);
    }

    public static void sendTitle(
            Component title, Component subTitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if (title != null) {
            sendTitleText(title);
        }

        if (subTitle != null) {
            sendSubtitleText(subTitle);
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
}
