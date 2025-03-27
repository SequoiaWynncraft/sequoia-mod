/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.PlayerRenderEvent;
import com.wynntils.mc.extension.EntityRenderStateExtension;
import com.wynntils.utils.colors.CommonColors;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.utils.mc.PlayerUtils;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

public class PartyLowHealthFeature extends Feature {
    private static final MultiBufferSource.BufferSource BUFFER_SOURCE =
            MultiBufferSource.immediate(new ByteBufferBuilder(256));

    private static final Pattern PLAYER_HEALTH_SCOREBOARD_LINE_PATTERN = Pattern.compile(
            "§e- §4\\[§([a-z0-9](\\|§[a-z0-9])*)?(\\|)*(\\d*?)(§[a-z0-9]\\d*)*\\|\\|§4] §f(.+?)(?:§7 \\[\\d+\\])?");
    private static final Pattern SCOREBOARD_HEALTH_PATTERN = Pattern.compile("§4\\[(.*?)§4]");

    private static final int CIRCLE_SEGMENTS = 128;
    private static final float CIRCLE_HEIGHT = 0.1F;
    private static final int CIRCLE_TRANSPARENCY = 95;
    private static final List<String> partyMembers = Lists.newArrayList();

    //    @SubscribeEvent
    //    public void onPartyOtherJoin(PartyEvent.OtherJoined event) {
    //        partyMembers.add(event.getPlayerName());
    //    }
    //
    //    @SubscribeEvent
    //    public void onPartyOtherLeave(PartyEvent.OtherLeft event) {
    //        partyMembers.removeIf(s -> s.matches(event.getPlayerName()));
    //    }
    //
    //    @SubscribeEvent
    //    public void onPartyCreate(ChatMessageReceivedEvent event) {
    //
    //        partyMembers.add(event.getPlayerName());
    //    }

    //    @SubscribeEvent
    //    public void onPartyPromote(PartyEvent.Promoted event) {
    //        return;
    //    }

    @SubscribeEvent
    public void onPlayerRender(PlayerRenderEvent event) {
        Entity entity = ((EntityRenderStateExtension) event.getPlayerRenderState()).getEntity();
        if (!(entity instanceof AbstractClientPlayer player)) return;
        if (!Models.WorldState.onWorld() && !Models.WorldState.onHousing()) return;
        if (PlayerUtils.isSelf(player)) return;
        if (!Models.Player.isLocalPlayer(player)) return;
        SequoiaMod.debug(String.valueOf(partyMembers));
        if (partyMembers.size() < 2) return;

        int line = 0;
        for (String scoreboardLine : PlayerUtils.getScoreboardLines()) {
            Matcher matcher = PLAYER_HEALTH_SCOREBOARD_LINE_PATTERN.matcher(scoreboardLine);
            if (!matcher.matches()) continue;
            if (line >= partyMembers.size()) return;

            String playerName = partyMembers.get(line);
            Matcher scoreboardHealthMatcher = SCOREBOARD_HEALTH_PATTERN.matcher(scoreboardLine);

            if (scoreboardHealthMatcher.find()) {
                String healthSegmentsSection = scoreboardHealthMatcher.group(1);
                double healthPercentage = calculateHealthPercentage(healthSegmentsSection);
                SequoiaMod.debug(String.format("%s, %s %f", player.getName(), playerName, healthPercentage));
                if (healthPercentage <= 37.5 && player.getName().getString().contains(playerName)) {
                    WynnUtils.renderCircle(
                            BUFFER_SOURCE,
                            CIRCLE_SEGMENTS,
                            CIRCLE_HEIGHT,
                            event.getPoseStack(),
                            player.position(),
                            7.9F,
                            CommonColors.RED.withAlpha(CIRCLE_TRANSPARENCY).asInt());
                }
            }
            line++;
        }
    }

    private double calculateHealthPercentage(String segmentSection) {
        long totalSegments = 0;
        long redSegments = 0;
        String currentColor = "";

        for (int i = 0; i < segmentSection.length(); i++) {
            char ch = segmentSection.charAt(i);
            if (ch == '§' && i + 1 < segmentSection.length()) {
                char nextChar = segmentSection.charAt(i + 1);
                currentColor = nextChar == 'c' ? "red" : nextChar == '8' ? "grey" : "";
                i++;
                continue;
            }

            totalSegments++;
            if (StringUtils.equals("red", currentColor)) {
                redSegments++;
            }
        }

        double percentage = totalSegments > 0 ? (redSegments / (double) totalSegments) * 100 : 100.0;
        return percentage;
    }
}
