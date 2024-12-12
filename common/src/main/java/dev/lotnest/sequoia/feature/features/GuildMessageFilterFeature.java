package dev.lotnest.sequoia.feature.features;

import com.google.common.collect.Sets;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class GuildMessageFilterFeature extends Feature {
    private static final List<Pattern> GUILD_CHAT_TERRITORY_DEFENSE =
            List.of(Pattern.compile("§3.+§b (.+) defense is (.+)"));

    private static final List<Pattern> WAR_STARTS = List.of(
            Pattern.compile("§cThewar for (?<territory>.+) will start in (?<remaining>.+) (?<type>minutes|seconds)\\."),
            Pattern.compile(
                    "§cThe war for (?<territory>.+) will start in (?<remaining>.+) (?<type>minutes|seconds)\\."),
            Pattern.compile("§cThe battle has begun!"));

    private static final List<Pattern> TERRITORY_CAPTURED_BY_OTHER_GUILD = List.of(
            Pattern.compile("§c\\[(?<guild>.+)] has taken control of (?<territory>.+)!"),
            Pattern.compile("§c\\[(?<guild>.+)] captured the territory (?<territory>.+)\\."),
            Pattern.compile("§cYour active attack was canceled and refunded to your headquarter."));

    private static final List<Pattern> TERRITORY_CAPTURED_BY_OWN_GUILD = List.of(
            Pattern.compile(
                    "§cYou have taken control of (?<territory>.+) from (?<guild>.+)! Use /guild territory to defend this territory\\."));

    private static final List<Pattern> TERRITORY_PRODUCTION = List.of(
            Pattern.compile("§b Territory §3(?<territory>.+) §bproduction has stabilised"),
            Pattern.compile("§b Territory §3(?<territory>.+) §bis producing more resources than it can store!"),
            Pattern.compile("§b Territory §3(?<territory>.+) §bis using more resources than it can store!"));

    private static final List<Pattern> TERRITORY_CHANGED = List.of(
            Pattern.compile("§b(?<player>.+) changed the style of (?<territory>.+) to (?<style>.+)"),
            Pattern.compile("§b(?<player>.+) set (?<bonus>.+) bonus to level (?<level>.+) on (?<territory>.+)"),
            Pattern.compile("§b(?<player>.+) changed (?<amount>.+) bonuses on (?<territory>.+)"),
            Pattern.compile("§b(?<player>.+) changed (?<amount>.+) upgrades on (?<territory>.+)"),
            Pattern.compile("§b(?<player>.+) set (?<bonus>.+) to level (?<level>.+) on (?<territory>.+)"),
            Pattern.compile("§b(?<player>.+) applied the Loadout §3(?<bonus>.+)§b on §3(?<territory>.+)"),
            Pattern.compile("§b(?<player>.+) updated Loadout §3(?<bonus>.+)"),
            Pattern.compile("§b(?<player>.+) removed (?<bonus>.+) bonus from (?<territory>.+)"),
            Pattern.compile("§b(?<player>.+) removed (?<bonus>.+) upgrade from (?<territory>.+)"),
            Pattern.compile("§b(?<player>.+) changed the borders of (?<territory>.+) to (?<style>.+)"));

    private static final List<Pattern> REWARD_GIVEN = List.of(
            Pattern.compile(
                    "§b(?<player>.+) rewarded §3(?<amount>.+) (?<reward>.+)§b to (?<recipient>.+)\n§3Rewards can be claimed in the Member Menu."));

    private static final List<Pattern> TERRITORY_DEFENDED = List.of(
            Pattern.compile("§cYour guild has successfully defended (?<territory>.+)\\."),
            Pattern.compile("§c\\[(?<guild>.+)] has lost the war!"),
            Pattern.compile("§cYour guild has lost the war for (?<territory>.+)\\."),
            Pattern.compile("§c\\[(?<guild>.+)] was victorious!"));

    private static final List<Pattern> EVERYONE_GUILD_BANK = List.of(
            Pattern.compile("§b(?<player>.+) withdrew (?<amount>.+)x (?<item>.+) from the Guild Bank \\(Everyone\\)"),
            Pattern.compile("§b(?<player>.+) deposited (?<amount>.+)x (?<item>.+) to the Guild Bank \\(Everyone\\)"));

    private static final List<Pattern> HIGH_RANKED_GUILD_BANK = List.of(
            Pattern.compile(
                    "§b(?<player>.+) withdrew (?<amount>.+)x (?<item>.+) from the Guild Bank \\(High Ranked\\)"),
            Pattern.compile(
                    "§b(?<player>.+) deposited (?<amount>.+)x (?<item>.+) to the Guild Bank \\(High Ranked\\)"));

    private static final List<Pattern> GUILD_TOME_FOUND = List.of(
            Pattern.compile(
                    "b A §3Guild Tome§b has been found and added to the Guild Rewards. §3Owner and Chiefs§b can gift it to members."));

    private static final Set<String> FFA_TERRITORIES = Sets.newHashSet(
            "Nexus of Light",
            "Azure Frontier",
            "Field of Life",
            "Primal Fen",
            "Path to Light",
            "Otherworldly Monolith",
            "Luminous Plateau",
            "Heavenly Ingress",
            "Unicorn Trail",
            "Collapsed Bridge",
            "Chasm Overlook",
            "Dodegar's Forge",
            "Mycelial Expanse",
            "Big Mushroom Cave",
            "Waterfall Cave",
            "Paper Trail",
            "Pine Pillar Forest",
            "Timeworn Arch",
            "Evergreen Outbreak",
            "Burning Farm",
            "Troms Lake",
            "Herb Cave",
            "Jungle Entrance",
            "Monte's Village",
            "Apprentice Huts",
            "Iboju Village",
            "Fountain of Youth",
            "Delnar Manor",
            "Entamis Village");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMessage(ChatMessageReceivedEvent event) {
        if (SequoiaMod.CONFIG.guildMessageFilterFeature.decisionType() == GuildMessageFilterDecisionType.KEEP_ALL) {
            return;
        }

        if (event.getOriginalStyledText() == null) {
            return;
        }

        StyledText message = event.getOriginalStyledText();

        if (processAndHandleFilter(message, GUILD_CHAT_TERRITORY_DEFENSE, event)) return;
        if (processAndHandleFilter(message, WAR_STARTS, event)) return;
        if (processAndHandleFilter(message, TERRITORY_CAPTURED_BY_OTHER_GUILD, event)) return;
        if (processAndHandleFilter(message, TERRITORY_CAPTURED_BY_OWN_GUILD, event)) return;
        if (processAndHandleFilter(message, TERRITORY_PRODUCTION, event)) return;
        if (processAndHandleFilter(message, TERRITORY_CHANGED, event)) return;
        if (processAndHandleFilter(message, REWARD_GIVEN, event)) return;
        if (processAndHandleFilter(message, TERRITORY_DEFENDED, event)) return;
        if (processAndHandleFilter(message, EVERYONE_GUILD_BANK, event)) return;
        if (processAndHandleFilter(message, HIGH_RANKED_GUILD_BANK, event)) return;
        if (processAndHandleFilter(message, GUILD_TOME_FOUND, event)) return;
    }

    private boolean processAndHandleFilter(StyledText message, List<Pattern> patterns, ChatMessageReceivedEvent event) {
        if (processFilter(message, patterns)) {
            if (SequoiaMod.CONFIG.guildMessageFilterFeature.decisionType() == GuildMessageFilterDecisionType.CANCEL_FFA
                    && FFA_TERRITORIES.stream()
                            .anyMatch(territory -> message.getString().contains(territory))) {
                event.setCanceled(true);
            } else if (SequoiaMod.CONFIG.guildMessageFilterFeature.decisionType()
                    == GuildMessageFilterDecisionType.CANCEL) {
                event.setCanceled(true);
            } else if (SequoiaMod.CONFIG.guildMessageFilterFeature.decisionType()
                            == GuildMessageFilterDecisionType.GRAY_OUT_FFA
                    && FFA_TERRITORIES.stream()
                            .anyMatch(territory -> message.getString().contains(territory))) {
                event.setMessage(StyledText.fromComponent(
                        Component.literal(message.getStringWithoutFormatting()).withStyle(ChatFormatting.DARK_GRAY)));
            } else if (SequoiaMod.CONFIG.guildMessageFilterFeature.decisionType()
                    == GuildMessageFilterDecisionType.GRAY_OUT) {
                event.setMessage(StyledText.fromComponent(
                        Component.literal(message.getStringWithoutFormatting()).withStyle(ChatFormatting.DARK_GRAY)));
            }
            return true;
        }
        return false;
    }

    private boolean processFilter(StyledText message, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            if (message.getMatcher(pattern).find()) {
                return true;
            }
        }
        return false;
    }

    public enum GuildMessageFilterDecisionType {
        KEEP_ALL,
        CANCEL,
        CANCEL_FFA,
        GRAY_OUT,
        GRAY_OUT_FFA
    }
}
