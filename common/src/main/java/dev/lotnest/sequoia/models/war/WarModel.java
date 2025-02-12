/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models.war;

import com.google.common.collect.Maps;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.features.messagefilter.guild.GuildMessageFilterPatterns;
import dev.lotnest.sequoia.features.war.GuildWar;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import net.minecraft.ChatFormatting;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class WarModel extends Model {
    private final Map<Integer, GuildWar> activeWars = Maps.newHashMap();

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
            activeWars.remove(territoryName.hashCode());
            return;
        }

        Matcher territoryDefenseMatcher = GuildMessageFilterPatterns.WAR[0].matcher(unformattedMessage);
        if (territoryDefenseMatcher.matches()) {
            String territoryName = territoryDefenseMatcher.group("territory");
            Difficulty defenseDifficulty = Difficulty.fromString(territoryDefenseMatcher.group("defense"));
            GuildWar guildWar = new GuildWar(territoryName.hashCode(), territoryName, defenseDifficulty, false);
            activeWars.put(territoryName.hashCode(), guildWar);
        }
    }

    public void mockWars() {
        String territoryName = "Terr1";
        Difficulty defenseDifficulty = Difficulty.fromString("Very High");
        GuildWar guildWar = new GuildWar(territoryName.hashCode(), territoryName, defenseDifficulty, false);
        activeWars.put(territoryName.hashCode(), guildWar);
        territoryName = "Terr2";
        defenseDifficulty = Difficulty.fromString("Medium");
        guildWar = new GuildWar(territoryName.hashCode(), territoryName, defenseDifficulty, false);
        activeWars.put(territoryName.hashCode(), guildWar);
    }

    public Map<Integer, GuildWar> getActiveWars() {
        SequoiaMod.debug(activeWars.toString());
        return activeWars;
    }

    public void updateParty(int hash, boolean value) {
        GuildWar old = activeWars.get(hash);
        activeWars.remove(hash);
        activeWars.put(old.hash(), new GuildWar(old.hash(), old.territory(), old.difficulty(), value));
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
