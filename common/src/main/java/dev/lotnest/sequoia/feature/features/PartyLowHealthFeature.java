package dev.lotnest.sequoia.feature.features;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.PlayerRenderEvent;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.render.buffered.CustomRenderType;
import com.wynntils.utils.type.ThrottledSupplier;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.utils.PlayerUtils;
import dev.lotnest.sequoia.wynn.WynnUtils;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Position;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;

public class PartyLowHealthFeature extends Feature {
    private static final MultiBufferSource.BufferSource BUFFER_SOURCE =
            MultiBufferSource.immediate(new ByteBufferBuilder(256));

    private static final Pattern PLAYER_HEALTH_SCOREBOARD_LINE_PATTERN = Pattern.compile(
            "§e- §4\\[§([a-z0-9](\\|§[a-z0-9])*)?(\\|)*(\\d*?)(§[a-z0-9]\\d*)*\\|\\|§4] §f(.+?)(?:§7 \\[\\d+\\])?");
    private static final Pattern SCOREBOARD_HEALTH_PATTERN = Pattern.compile("§4\\[(.*?)§4]");

    private static final int CIRCLE_SEGMENTS = 128;
    private static final float CIRCLE_HEIGHT = 0.1F;
    private static final int CIRCLE_TRANSPARENCY = 95;

    private final ThrottledSupplier<List<String>> partyMembersSupplier =
            new ThrottledSupplier<>(WynnUtils::getPartyMembersFromTabList, Duration.ofMillis(250));

    @SubscribeEvent
    public void onPlayerRender(PlayerRenderEvent event) {
        if (!Models.WorldState.onWorld() && !Models.WorldState.onHousing()) {
            return;
        }

        AbstractClientPlayer player = event.getPlayer();
        if (PlayerUtils.isSelf(player)) {
            return;
        }

        if (!Models.Player.isLocalPlayer(player)) {
            return;
        }

        List<String> partyMembers = partyMembersSupplier.get();
        if (partyMembers.size() < 2) {
            return;
        }

        int line = 0;
        for (String scoreboardLine : PlayerUtils.getScoreboardLines()) {
            Matcher matcher = PLAYER_HEALTH_SCOREBOARD_LINE_PATTERN.matcher(scoreboardLine);
            if (!matcher.matches()) {
                continue;
            }

            if (line >= partyMembers.size()) {
                return;
            }
            line++;
            String playerName = partyMembers.get(line);
            Matcher scoreboardHealthMatcher = SCOREBOARD_HEALTH_PATTERN.matcher(scoreboardLine);

            if (scoreboardHealthMatcher.find()) {
                String healthSegmentsSection = scoreboardHealthMatcher.group(1);
                double healthPercentage = calculateHealthPercentage(healthSegmentsSection);

                if (healthPercentage <= 37.5 && player.getName().getString().contains(playerName)) {
                    renderCircle(
                            event.getPoseStack(),
                            player.position(),
                            7.9F,
                            CommonColors.RED.withAlpha(CIRCLE_TRANSPARENCY).asInt());
                }
            }
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

    /**
     * Renders a circle with the given radius. Some notes for future reference:<p>
     * - The circle is rendered at the player's feet, from the ground to HEIGHT blocks above the ground.<p>
     * - .color() takes floats from 0-1, but ints from 0-255<p>
     * - Increase SEGMENTS to make the circle smoother, but it will also increase the amount of vertices (and thus the amount of memory used and the amount of time it takes to render)<p>
     * - The order of the consumer.vertex() calls matter. Here, we draw a quad, so we do bottom left corner, top left corner, top right corner, bottom right corner. This is filled in with the color we set.<p>
     *
     * @param poseStack The pose stack to render with. This is supposed to be the pose stack from the event.
     *                  We do the translation here, so no need to do it before passing it in.
     * @param radius
     * @param color
     */
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
