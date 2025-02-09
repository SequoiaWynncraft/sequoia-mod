/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.models.War;

import com.google.common.collect.Sets;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.mc.StyledTextUtils;
import dev.lotnest.sequoia.core.components.Model;
import dev.lotnest.sequoia.features.war.GuildWar;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class WarModel extends Model {
    private static final Pattern CAPTURED_PATTERN = Pattern.compile(
            "§c(?:\uE006\uE002|\uE001) \\[(?<guild>.+)\\] (?:has )?captured the territory (?<territory>.+)\\.");
    private static final Pattern GUILD_DEFENSE_CHAT_PATTERN = Pattern.compile("§b.+§b (.+) defense is (.+)");
    private final Set<GuildWar> wars = Sets.newHashSet();

    public WarModel() {
        super(List.of());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWarQueued(ChatMessageReceivedEvent event) {
        StyledText cleanMessage = StyledTextUtils.unwrap(event.getOriginalStyledText());
        Matcher matcher = cleanMessage.getMatcher(CAPTURED_PATTERN);
        if (matcher.matches()) {
            String territory = matcher.group("territory");
            wars.removeIf(guildWar -> guildWar.hash() == territory.hashCode());
            return;
        }
        matcher = cleanMessage.getMatcher(GUILD_DEFENSE_CHAT_PATTERN);
        if (matcher.matches()) {
            String territory = matcher.group(1);
            GuildWar war = new GuildWar(territory.hashCode(), territory, Difficulty.fromString(matcher.group(2)));
            wars.add(war);
            return;
        }
    }

    public enum Difficulty {
        NONE("None", ChatFormatting.WHITE),
        VERY_LOW("Very Low", ChatFormatting.DARK_GREEN),
        LOW("Low", ChatFormatting.GREEN),
        MEDIUM("Medium", ChatFormatting.YELLOW),
        HIGH("High", ChatFormatting.RED),
        VERY_HIGH("Very High", ChatFormatting.DARK_RED);

        private final String displayName;
        private final ChatFormatting defenceColor;

        Difficulty(String displayName, ChatFormatting defenceColor) {
            this.displayName = displayName;
            this.defenceColor = defenceColor;
        }

        @Override
        public String toString() {
            return getDisplayName() + " " + getDefenceColor();
        }

        public static Difficulty fromString(String string) {
            Difficulty[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                Difficulty value = var1[var3];
                if (value.displayName.equals(string)) {
                    return value;
                }
            }

            return null;
        }

        public String getDisplayName() {
            return displayName;
        }

        public ChatFormatting getDefenceColor() {
            return defenceColor;
        }
    }
}
