package dev.lotnest.sequoia.feature.features;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.PlayerRenderEvent;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.buffered.CustomRenderType;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.utils.PlayerUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Position;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;

public class RaidLowHealthFeature extends Feature {
    private static final MultiBufferSource.BufferSource BUFFER_SOURCE =
            MultiBufferSource.immediate(new ByteBufferBuilder(256));

    private static final Pattern PLAYER_HEALTH_SCOREBOARD_LINE_PATTERN = Pattern.compile(
            "§e- §4\\[§([a-z0-9](\\|§[a-z0-9])*)?(\\|)*(\\d*?)(§[a-z0-9]\\d*)*\\|\\|§4] §f(.+?)(?:§7 \\[\\d+])?");
    private static final Pattern SEGMENT_PATTERN = Pattern.compile("§4\\[(.*?)§4]");

    private static final int CIRCLE_SEGMENTS = 64;
    private static final float CIRCLE_HEIGHT = 0.1F;
    private static final int CIRCLE_TRANSPARENCY = 95;

    private final Set<Player> detectedPlayers = Sets.newHashSet();
    private final Map<Player, List<Pair<CustomColor, Float>>> circlesToRender = Maps.newHashMap();

    @SubscribeEvent
    public void onPlayerRender(PlayerRenderEvent event) {
        AbstractClientPlayer player = event.getPlayer();
        detectedPlayers.add(player);

        List<Pair<CustomColor, Float>> circles = circlesToRender.get(player);
        if (circles == null) {
            return;
        }

        circles.forEach(circleType -> {
            float radius = circleType.second;
            int color = circleType.first.asInt();
            renderCircle(event.getPoseStack(), player.position(), radius, color);
        });
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        circlesToRender.clear();
        detectedPlayers.forEach(this::checkCircles);
        detectedPlayers.clear();
    }

    private void checkCircles(Player player) {
        if (!Models.Player.isLocalPlayer(player)) {
            return;
        }

        List<Pair<CustomColor, Float>>[] circles = new List[] {null};
        if (player == McUtils.player()) {
            return;
        } else {
            for (String scoreboardLine : PlayerUtils.getScoreboardLines()) {
                Matcher scoreboardLineMatcher = PLAYER_HEALTH_SCOREBOARD_LINE_PATTERN.matcher(scoreboardLine);
                if (scoreboardLineMatcher.matches()) {
                    String playerName = scoreboardLineMatcher.group(6);
                    Matcher segmentSectionMatcher = SEGMENT_PATTERN.matcher(scoreboardLine);

                    if (segmentSectionMatcher.find()) {
                        String segmentSection = segmentSectionMatcher.group(1);

                        long totalSegments = 0;
                        long redSegments = 0;
                        long greySegments = 0;
                        String currentColor = "";

                        for (int i = 0; i < segmentSection.length(); i++) {
                            char ch = segmentSection.charAt(i);

                            if (ch == '§' && i + 1 < segmentSection.length()) {
                                char nextChar = segmentSection.charAt(i + 1);
                                if (nextChar == 'c') {
                                    currentColor = "red";
                                } else if (nextChar == '8') {
                                    currentColor = "grey";
                                }
                                i++;
                                continue;
                            }

                            totalSegments++;

                            if (StringUtils.equals("red", currentColor)) {
                                redSegments++;
                            } else if (StringUtils.equals("grey", currentColor)) {
                                greySegments++;
                            }
                        }

                        double healthPercentage;
                        if (totalSegments > 0) {
                            // Health is determined by red segments out of total segments
                            healthPercentage = (redSegments / (double) totalSegments) * 100;
                        } else {
                            // If no segments are found, consider the health as 100%
                            healthPercentage = 100.0;
                        }

                        SequoiaMod.debug("Scoreboard Line: " + scoreboardLine);
                        SequoiaMod.debug("Player: " + playerName + ", Red Segments: "
                                + redSegments + ", Grey Segments: " + greySegments + ", Total Segments: "
                                + totalSegments
                                + ", Health Percentage: " + healthPercentage + "%");

                        if (healthPercentage <= 40.0
                                && player.getName().getString().contains(playerName)) {
                            circles[0] = Collections.singletonList(
                                    Pair.of(CommonColors.RED.withAlpha(CIRCLE_TRANSPARENCY), 7.9F));
                        }
                    }
                }
            }

            circlesToRender.put(player, circles[0]);
        }
    }

    private void renderCircle(PoseStack poseStack, Position position, float radius, int color) {
        // Circle must be rendered on both sides, otherwise it will be invisible when looking at
        // it from the outside
        RenderSystem.disableCull();

        poseStack.pushPose();
        poseStack.translate(-position.x(), -position.y(), -position.z());
        VertexConsumer consumer = BUFFER_SOURCE.getBuffer(CustomRenderType.POSITION_COLOR_QUAD);

        Matrix4f matrix4f = poseStack.last().pose();
        double angleStep = 2 * Math.PI / CIRCLE_SEGMENTS;
        double startingAngle = -(System.currentTimeMillis() % 40000) * 2 * Math.PI / 40000.0;
        double angle = startingAngle;
        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            if (i % 4 > 2) {
                angle += angleStep;
                continue;
            }
            float x = (float) (position.x() + Math.sin(angle) * radius);
            float z = (float) (position.z() + Math.cos(angle) * radius);
            consumer.addVertex(matrix4f, x, (float) position.y(), z).setColor(color);
            consumer.addVertex(matrix4f, x, (float) position.y() + CIRCLE_HEIGHT, z)
                    .setColor(color);
            angle += angleStep;
            float x2 = (float) (position.x() + Math.sin(angle) * radius);
            float z2 = (float) (position.z() + Math.cos(angle) * radius);
            consumer.addVertex(matrix4f, x2, (float) position.y() + CIRCLE_HEIGHT, z2)
                    .setColor(color);
            consumer.addVertex(matrix4f, x2, (float) position.y(), z2).setColor(color);
        }

        BUFFER_SOURCE.endBatch();
        poseStack.popPose();
        RenderSystem.enableCull();
    }
}
