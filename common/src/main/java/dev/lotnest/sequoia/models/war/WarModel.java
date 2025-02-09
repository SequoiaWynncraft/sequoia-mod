/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models.war;

import com.google.common.collect.Sets;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.features.messagefilter.guild.GuildMessageFilterPatterns;
import dev.lotnest.sequoia.features.war.GuildWar;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import net.minecraft.ChatFormatting;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class WarModel extends Model {
    private final Set<GuildWar> activeWars = Sets.newHashSet();

    public WarModel() {
        super(List.of());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void handleChatMessage(ChatMessageReceivedEvent event) {
        String unformattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());

        Matcher takenControlOfTerritoryMatcher = GuildMessageFilterPatterns.WAR[3].matcher(unformattedMessage);
        if (takenControlOfTerritoryMatcher.matches()) {
            String territoryName = takenControlOfTerritoryMatcher.group("territory");
            activeWars.removeIf(war -> war.hash() == territoryName.hashCode());
            return;
        }

        Matcher territoryDefenseMatcher = GuildMessageFilterPatterns.WAR[0].matcher(unformattedMessage);
        if (territoryDefenseMatcher.matches()) {
            String territoryName = territoryDefenseMatcher.group(1);
            Difficulty defenseDifficulty = Difficulty.fromString(territoryDefenseMatcher.group(2));
            GuildWar guildWar = new GuildWar(territoryName.hashCode(), territoryName, defenseDifficulty);
            activeWars.add(guildWar);
        }
    }

    public Set<GuildWar> getActiveWars() {
        return Collections.unmodifiableSet(activeWars);
    }

    public enum Difficulty {
        NONE("None", ChatFormatting.WHITE),
        VERY_LOW("Very Low", ChatFormatting.DARK_GREEN),
        LOW("Low", ChatFormatting.GREEN),
        MEDIUM("Medium", ChatFormatting.YELLOW),
        HIGH("High", ChatFormatting.RED),
        VERY_HIGH("Very High", ChatFormatting.DARK_RED);

        private final String displayName;
        private final ChatFormatting color;

        Difficulty(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public static Difficulty fromString(String value) {
            for (Difficulty difficulty : values()) {
                if (difficulty.displayName.equalsIgnoreCase(value)) {
                    return difficulty;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return color + displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public ChatFormatting getColor() {
            return color;
        }
    }
}
