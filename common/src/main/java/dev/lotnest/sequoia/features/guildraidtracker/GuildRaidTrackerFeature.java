/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.guildraidtracker;

import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.core.events.GuildRaidCompletedEvent;
import dev.lotnest.sequoia.core.websocket.WSMessage;
import dev.lotnest.sequoia.core.websocket.messages.GuildRaidWSMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class GuildRaidTrackerFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuildRaidCompleted(GuildRaidCompletedEvent event) {
        if (!isEnabled()) {
            SequoiaMod.debug("Ignoring Guild Raid completion report as the feature is disabled.");
            return;
        }

        if (SequoiaMod.getWebSocketFeature() == null
                || !SequoiaMod.getWebSocketFeature().isEnabled()) {
            SequoiaMod.debug("Ignoring Guild Raid completion report as the WebSocket feature is disabled.");
            return;
        }

        if (!SequoiaMod.getWebSocketFeature().isAuthenticated()) {
            SequoiaMod.debug(
                    "Ignoring Guild Raid completion report as the user is not authenticated with the WebSocket.");
            return;
        }

        sendGuildRaidCompletionReport(event.getGuildRaid());
    }

    private void sendGuildRaidCompletionReport(GuildRaid guildRaid) {
        if (guildRaid == null) {
            SequoiaMod.debug("Ignoring Guild Raid completion report as the Guild Raid is null.");
            return;
        }

        try {
            WSMessage guildRaidWSMessage = new GuildRaidWSMessage(guildRaid);
            String payload = SequoiaMod.getWebSocketFeature().sendAsJson(guildRaidWSMessage);
            if (StringUtils.isNotBlank(payload)) {
                SequoiaMod.debug("Sending Guild Raid completion: " + payload);
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to send Guild Raid completion report", exception);
            McUtils.sendMessageToClient(
                    Component.literal("Failed to report Guild Raid completion, check the logs for more info.")
                            .withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.guildRaidTrackerFeature.enabled();
    }
}
