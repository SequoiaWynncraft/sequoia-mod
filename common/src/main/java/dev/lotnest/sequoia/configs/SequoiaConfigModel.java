package dev.lotnest.sequoia.configs;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.features.messagefilter.MessageFilterDecisionType;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Expanded;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.SectionHeader;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.compress.utils.Lists;

@Modmenu(modId = SequoiaMod.MOD_ID)
@Config(name = "sequoia", wrapperName = "SequoiaConfig")
public class SequoiaConfigModel {
    @SectionHeader("general")
    public boolean verboseLogging = false;

    public boolean renderSequoiaPanorama = true;
    public boolean renderSequoiaSplashes = true;

    @SectionHeader("features")
    @Nest
    @Expanded
    public MessageFilterFeature messageFilterFeature = new MessageFilterFeature();

    @Nest
    @Expanded
    public GuildMessageFilterFeature guildMessageFilterFeature = new GuildMessageFilterFeature();

    @Nest
    @Expanded
    public ModMessageFilterFeature modMessageFilterFeature = new ModMessageFilterFeature();

    @Nest
    @Expanded
    public PlayerIgnoreFeature playerIgnoreFeature = new PlayerIgnoreFeature();

    @Nest
    @Expanded
    public SequoiaOSTFeature sequoiaOSTFeature = new SequoiaOSTFeature();

    @Nest
    @Expanded
    public WebSocketFeature webSocketFeature = new WebSocketFeature();

    @Nest
    @Expanded
    public GuildRaidTrackerFeature guildRaidTrackerFeature = new GuildRaidTrackerFeature();

    @Nest
    @Expanded
    public DiscordChatBridgeFeature discordChatBridgeFeature = new DiscordChatBridgeFeature();

    @Nest
    @Expanded
    public OuterVoidTrackerFeature outerVoidTrackerFeature = new OuterVoidTrackerFeature();

    @Nest
    @Expanded
    public RaidsFeature raidsFeature = new RaidsFeature();

    public static class MessageFilterFeature {
        public boolean enabled = false;
        public MessageFilterDecisionType eventMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType partyFinderMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType crateMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType petMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
    }

    public static class GuildMessageFilterFeature {
        public boolean enabled = false;
        public MessageFilterDecisionType raidMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType warMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType economyMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType rewardMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType bankMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType rankMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
    }

    public static class ModMessageFilterFeature {
        public boolean enabled = false;
        public MessageFilterDecisionType wynntilsConnectionMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
        public MessageFilterDecisionType fuyggConnectionMessagesFilterDecisionType = MessageFilterDecisionType.KEEP;
    }

    public static class PlayerIgnoreFeature {
        private static final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,16}");

        public boolean enabled = false;
        public boolean allowGuildChatMessagesFromIgnoredPlayers = false;
        public boolean allowPartyChatMessagesFromIgnoredPlayers = false;
        public boolean allowShoutsFromIgnoredPlayers = false;

        @PredicateConstraint("minecraftNameValidator")
        public List<String> ignoredPlayers = Lists.newArrayList();

        public static boolean minecraftNameValidator(List<String> minecraftNames) {
            return minecraftNames.stream()
                    .allMatch(name -> MINECRAFT_NAME_PATTERN.matcher(name).matches());
        }
    }

    public static class SequoiaOSTFeature {
        public boolean enabled = false;
    }

    public static class WebSocketFeature {
        public boolean enabled = true;
    }

    public static class GuildRaidTrackerFeature {
        public boolean enabled = true;
    }

    public static class DiscordChatBridgeFeature {
        public boolean enabled = true;
        public boolean sendInGameGuildChatMessagesToDiscord = true;
        public boolean sendDiscordMessagesToInGameChat = true;
    }

    public static class OuterVoidTrackerFeature {
        public boolean enabled = true;
        public boolean playSoundEffect = true;

        @RangeConstraint(min = 1, max = 20)
        public float scale = 2.0F;
    }

    public static class RaidsFeature {
        public boolean enabled = true;
        public boolean trackChosenPartyBuffs = true;

        @Nest
        @Expanded
        public NOTGRaidFeature NOTGRaidFeature = new NOTGRaidFeature();

        @Nest
        @Expanded
        public TCCRaidFeature TCCRaidFeature = new TCCRaidFeature();

        @Nest
        @Expanded
        public NOLRaidFeature NOLRaidFeature = new NOLRaidFeature();

        @Nest
        @Expanded
        public TNARaidFeature TNARaidFeature = new TNARaidFeature();

        public static class NOTGRaidFeature {}

        public static class TCCRaidFeature {}

        public static class NOLRaidFeature {
            public boolean showLightOrbFormingTitle = true;
            public boolean showCrystallineDecaysSpawnedTitle = true;
        }

        public static class TNARaidFeature {
            public boolean showShadowlingKilledTitle = true;
        }
    }
}
