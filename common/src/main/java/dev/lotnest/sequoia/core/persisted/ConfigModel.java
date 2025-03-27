/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.persisted;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.features.ItemSizeFeature;
import dev.lotnest.sequoia.features.messagefilter.MessageFilterDecisionType;
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
public class ConfigModel {
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

    @Nest
    @Expanded
    public TerritoryFeature territoryFeature = new TerritoryFeature();

    @Nest
    @Expanded
    public ItemSizeFeature itemSizeFeature = new ItemSizeFeature();

    @Nest
    @Expanded
    public GuildRewardStorageTrackerFeature guildRewardStorageTrackerFeature = new GuildRewardStorageTrackerFeature();

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
        public boolean autoReconnect = true;
        public boolean relayGuildMapData = true;
        public boolean relayGuildWarResultsData = true;
        public boolean relayLocationServiceData = true;
        public boolean relayLootPoolData = true;
    }

    public static class GuildRaidTrackerFeature {
        public boolean enabled = true;
    }

    public static class DiscordChatBridgeFeature {
        public boolean enabled = true;
        public boolean sendInGameGuildChatMessagesToDiscord = true;
        public boolean sendDiscordMessagesToInGameChat = true;
    }

    public static class GuildRewardStorageTrackerFeature {
        public boolean enabled = true;

        @RangeConstraint(min = 1, max = 100)
        public int value = 90;
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
        public dev.lotnest.sequoia.features.raids.RaidsFeature.RangeIndicatorDisplayType
                farsightedGambitOverlayDisplayType =
                        dev.lotnest.sequoia.features.raids.RaidsFeature.RangeIndicatorDisplayType.AUTOMATIC;
        public dev.lotnest.sequoia.features.raids.RaidsFeature.RangeIndicatorDisplayType
                myopicGambitOverlayDisplayType =
                        dev.lotnest.sequoia.features.raids.RaidsFeature.RangeIndicatorDisplayType.AUTOMATIC;
        public dev.lotnest.sequoia.features.raids.RaidsFeature.GluttonyWarningType gluttonyDisplayType =
                dev.lotnest.sequoia.features.raids.RaidsFeature.GluttonyWarningType.TEXT;
        public boolean maddeningMageGambitMiscastTracker = true;

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

        @Nest
        @Expanded
        public PartyRaidCompletionsDisplayFeature PartyRaidCompletionsDisplayFeature =
                new PartyRaidCompletionsDisplayFeature();

        public static class NOTGRaidFeature {}

        public static class TCCRaidFeature {}

        public static class NOLRaidFeature {
            public boolean showLightOrbFormingTitle = true;
            public boolean showCrystallineDecaysSpawnedTitle = true;
            public boolean autoSkipCutscene = true;
        }

        public static class TNARaidFeature {
            public boolean showShadowlingKilledTitle = true;
        }

        public static class PartyRaidCompletionsDisplayFeature {
            public dev.lotnest.sequoia.features.raids.PartyRaidCompletionsDisplayFeature.PartyRaidCompletionsDisplayType
                    displayType = dev.lotnest.sequoia.features.raids.PartyRaidCompletionsDisplayFeature
                            .PartyRaidCompletionsDisplayType.MANUAL;
        }
    }

    public static class TerritoryFeature {
        public boolean enabled = true;
        public boolean showCapturedTerritoryInfo = true;
    }

    public static class ItemSizeFeature {
        public boolean enabled = true;
        public float itemSize = 1.0f;
        public float RotationX = 0.0f;
        public float RotationY = 0.0f;
        public float RotationZ = 0.0f;
        public float PositionX = 0.0f;
        public float PositionY = 0.0f;
        public float PositionZ = 0.0f;
    }
}
