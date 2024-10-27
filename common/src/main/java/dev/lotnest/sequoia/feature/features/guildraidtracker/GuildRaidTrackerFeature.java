package dev.lotnest.sequoia.feature.features.guildraidtracker;

import com.google.gson.GsonBuilder;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Category;
import dev.lotnest.sequoia.feature.CategoryType;
import dev.lotnest.sequoia.feature.Feature;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

@Category(CategoryType.TRACKERS)
public class GuildRaidTrackerFeature extends Feature {
    private static final String GUILD_RAID_TRACKER_API_URL =
            "http://167.172.177.72:8084/sequoia-tree/api/public/v1/guildRaidTracker";

    private static final List<String> GUILD_RAID_NAMES =
            Arrays.stream(GuildRaidType.values()).map(GuildRaidType::getName).toList();
    private static final List<String> GUILD_RAID_KEYWORDS = GUILD_RAID_NAMES.stream()
            .map(name -> name.substring(name.lastIndexOf(' ') + 1))
            .toList();

    private static final Pattern SEASONAL_RATING_PATTERN = Pattern.compile("\\+(\\d+)");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper(new GsonBuilder().create());

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuildRaidCompletion(ChatMessageReceivedEvent event) {
        CompletableFuture.runAsync(() -> {
            List<String> guildRaidParticipants = Lists.newArrayList();
            String guildRaidName = null;
            int seasonalRating = 0;

            if (event.getStyledText() == null || event.getStyledText().getComponent() == null) {
                return;
            }

            String message = event.getStyledText().getString();
            if (!StringUtils.contains(message, "Seasonal Rating")) {
                return;
            }

            for (Component sibling : event.getStyledText().getComponent().getSiblings()) {
                String messagePart = sibling.getString();
                if (StringUtils.isBlank(messagePart)) {
                    continue;
                }

                TextColor messageColor = sibling.getStyle().getColor();
                if (messageColor == null) {
                    continue;
                }

                String colorHex = messageColor.toString();
                if (StringUtils.equals("#FFFF55", colorHex)) {
                    String playerName = extractPlayerName(messagePart);
                    if (playerName != null) {
                        guildRaidParticipants.add(playerName);
                    }
                } else if (StringUtils.equals("#00AAAA", colorHex)) {
                    for (int i = 0; i < GUILD_RAID_KEYWORDS.size(); i++) {
                        if (StringUtils.contains(messagePart, GUILD_RAID_KEYWORDS.get(i))) {
                            guildRaidName = GUILD_RAID_NAMES.get(i);
                            break;
                        }
                    }
                }

                Matcher seasonalRatingMatcher = SEASONAL_RATING_PATTERN.matcher(messagePart);
                if (seasonalRatingMatcher.find()) {
                    seasonalRating = Integer.parseInt(seasonalRatingMatcher.group(1));
                }
            }

            if (guildRaidName != null) {
                sendGuildRaidCompletionReport(
                        new GuildRaid(GuildRaidType.fromString(guildRaidName), guildRaidParticipants, seasonalRating));
            }
        });
    }

    private String extractPlayerName(String messagePart) {
        return StringUtils.strip(messagePart);
    }

    private void sendGuildRaidCompletionReport(GuildRaid guildRaid) {
        try {
            String payload = objectMapper.writeValueAsString(guildRaid);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GUILD_RAID_TRACKER_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 204) {
                SequoiaMod.info("Reported guild raid completion of \"" + guildRaid.type() + "\" for players: "
                        + guildRaid.players());
            } else {
                SequoiaMod.error(
                        "Unexpected guild raid tracker response: " + response.statusCode() + ": " + response.body());
            }
        } catch (IOException | InterruptedException exception) {
            SequoiaMod.error("Failed to report guild raid completion", exception);
            Thread.currentThread().interrupt();
        }
    }
}
