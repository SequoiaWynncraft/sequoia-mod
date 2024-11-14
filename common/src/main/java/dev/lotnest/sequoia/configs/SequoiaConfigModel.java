package dev.lotnest.sequoia.configs;

import dev.lotnest.sequoia.SequoiaMod;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Expanded;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.SectionHeader;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.regex.Pattern;

@Modmenu(modId = SequoiaMod.MOD_ID)
@Config(name = "sequoia", wrapperName = "SequoiaConfig")
public class SequoiaConfigModel {
    @SectionHeader("features")
    @Nest
    @Expanded
    public CommandsFeature commandsFeature = new CommandsFeature();

    @Nest
    @Expanded
    public GuildMessageFilterFeature guildMessageFilterFeature = new GuildMessageFilterFeature();

    @Nest
    @Expanded
    public PlayerIgnoreFeature playerIgnoreFeature = new PlayerIgnoreFeature();

    @Nest
    @Expanded
    public SequoiaOSTFeature sequoiaOSTFeature = new SequoiaOSTFeature();

    @Nest
    @Expanded
    public GuildRaidTrackerFeature guildRaidTrackerFeature = new GuildRaidTrackerFeature();

    public static class CommandsFeature {
        public boolean isEnabled = true;
    }

    public static class GuildMessageFilterFeature {
        public boolean isEnabled = true;
    }

    public static class PlayerIgnoreFeature {
        private static final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,16}");

        public boolean isEnabled = true;

        @PredicateConstraint("minecraftNameValidator")
        public List<String> ignoredPlayers = Lists.newArrayList();

        public static boolean minecraftNameValidator(List<String> minecraftNames) {
            return minecraftNames.stream()
                    .allMatch(name -> MINECRAFT_NAME_PATTERN.matcher(name).matches());
        }
    }

    public static class SequoiaOSTFeature {
        public boolean isEnabled = true;
    }

    public static class GuildRaidTrackerFeature {
        public boolean isEnabled = true;
    }
}
