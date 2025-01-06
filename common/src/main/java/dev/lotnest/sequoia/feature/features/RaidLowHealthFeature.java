package dev.lotnest.sequoia.feature.features;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ibm.icu.impl.Pair;
import com.ibm.icu.text.Normalizer2;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.PlayerRenderEvent;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.buffered.CustomRenderType;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;

import dev.lotnest.sequoia.utils.PlayerUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaidLowHealthFeature extends Feature {
    private static final MultiBufferSource.BufferSource BUFFER_SOURCE =
            MultiBufferSource.immediate(new ByteBufferBuilder(256));

    private static final Pattern PLAYER_HEALTH_SCOREBOARD_LINE_PATTERN = Pattern.compile(
            "§e- §4\\[§([a-z0-9](\\|§[a-z0-9])*)?(\\|)*(\\d*?)(§[a-z0-9]\\d*)*\\|\\|§4] §f(.+?)(?:§7 \\[\\d+])?");

    // Number of straight lines to draw when rendering circle
    private static final int SEGMENTS = 64;
    private static final float HEIGHT = 0.1f;
    private static final int TRANSPARENCY = 95;

    private final Set<Player> detectedPlayers = Sets.newHashSet();
    private final Map<Player, List<Pair<CustomColor, Float>>> circlesToRender = Maps.newHashMap();

    @SubscribeEvent
    public void onPlayerRender(PlayerRenderEvent event) {
        AbstractClientPlayer player = event.getPlayer();
        detectedPlayers.add(player);
        List<Pair<CustomColor, Float>> circles = circlesToRender.get(player);
        if (circles == null) return;

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
        // Don't render for ghost/npc
        if (!Models.Player.isLocalPlayer(player)) {
            return;
        }

        final List<Pair<CustomColor, Float>>[] circles = new List[]{null};
        if (player == McUtils.player()) {
            return;
        } else {
            //if (!Models.Party.getPartyMembers()
            //        .contains(StyledText.fromComponent(player.getName()).getStringWithoutFormatting())) return;
            Pattern healthScoreboardPlayerScorePattern = Pattern.compile(
                    "§e- §4\\[§([a-z0-9](\\|§[a-z0-9])*)?(\\|)*(\\d*?)(§[a-z0-9]\\d*)*\\|\\|§4] §f(.+?)(?:§7 \\[\\d+])");

            PlayerUtils.getScoreboardLines().forEach(scoreboardLine -> {
                Matcher scoreboardLineMatcher = PLAYER_HEALTH_SCOREBOARD_LINE_PATTERN.matcher(scoreboardLine);
                if (scoreboardLineMatcher.matches()) {
                    String healthRaw = scoreboardLineMatcher.group(5); // Cleaned health value
                    String healthCleaned = healthRaw != null ? healthRaw.replaceAll("§[a-z0-9]", "") : "Unknown";
                    String playerName = scoreboardLineMatcher.group(6);

                    // Extract health bar colors and segments (between §4[ and §4])
                    Matcher segmentSectionMatcher = Pattern.compile("§4\\[(.*?)§4\\]").matcher(scoreboardLine);
                    if (segmentSectionMatcher.find()) {
                        String segmentSection = segmentSectionMatcher.group(1); // Get content between §4[ and §4]

                        long totalSegments = 0;
                        long redSegments = 0;
                        long greySegments = 0;

                        String currentColor = ""; // Track the current color state

                        // Iterate over each character as a segment
                        for (int i = 0; i < segmentSection.length(); i++) {
                            char ch = segmentSection.charAt(i);

                            if (ch == '§' && i + 1 < segmentSection.length()) {
                                char nextChar = segmentSection.charAt(i + 1); // Get the color code
                                if (nextChar == 'c') {
                                    currentColor = "red"; // Switch to red state
                                } else if (nextChar == '8') {
                                    currentColor = "grey"; // Switch to grey state
                                }
                                i++; // Skip the color code character
                                continue;
                            }

                            totalSegments++; // Count each character as a segment

                            // Classify the segment based on the current color state
                            if (StringUtils.equals("red", currentColor)) {
                                redSegments++;
                            }
                        }

                        // Calculate health percentage based on red and total segments
                        double healthPercentage = totalSegments > 0 ? (redSegments / (double) totalSegments) * 100 : 0;

                        // Log results
                        SequoiaMod.debug("Scoreboard Line: " + scoreboardLine);
                        SequoiaMod.debug("Player: " + playerName + ", Health: " + healthCleaned + ", Red Segments: "
                                + redSegments + ", Grey Segments: " + greySegments + ", Total Segments: " + totalSegments
                                + ", Health Percentage: " + healthPercentage + "%");
                        if (healthPercentage <= 40 && player.getName().getString().contains(playerName)) {
                            circles[0] = Collections.singletonList(Pair.of(CommonColors.RED.withAlpha(TRANSPARENCY), 7.9f));
                        }

                    }
                }
            });

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
        double angleStep = 2 * Math.PI / SEGMENTS;
        double startingAngle = -(System.currentTimeMillis() % 40000) * 2 * Math.PI / 40000.0;
        double angle = startingAngle;
        for (int i = 0; i < SEGMENTS; i++) {
            if (i % 4 > 2) {
                angle += angleStep;
                continue;
            }
            float x = (float) (position.x() + Math.sin(angle) * radius);
            float z = (float) (position.z() + Math.cos(angle) * radius);
            consumer.addVertex(matrix4f, x, (float) position.y(), z).setColor(color);
            consumer.addVertex(matrix4f, x, (float) position.y() + HEIGHT, z).setColor(color);
            angle += angleStep;
            float x2 = (float) (position.x() + Math.sin(angle) * radius);
            float z2 = (float) (position.z() + Math.cos(angle) * radius);
            consumer.addVertex(matrix4f, x2, (float) position.y() + HEIGHT, z2).setColor(color);
            consumer.addVertex(matrix4f, x2, (float) position.y(), z2).setColor(color);
        }

        BUFFER_SOURCE.endBatch();
        poseStack.popPose();
        RenderSystem.enableCull();
    }
}
