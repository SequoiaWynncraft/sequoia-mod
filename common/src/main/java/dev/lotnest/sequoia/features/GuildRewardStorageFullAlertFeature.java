/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.container.scriptedquery.QueryBuilder;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.utils.mc.LoreUtils;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.wynn.InventoryUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class GuildRewardStorageFullAlertFeature extends Feature {
    private static final int GUILD_REWARDS_ITEM_SLOT = 27;
    private static final Pattern GUILD_REWARDS_EMERALDS_PATTERN = Pattern.compile("^§aEmeralds: §f(\\d+)§7/(\\d+)$");
    private static final Pattern GUILD_REWARDS_TOMES_PATTERN = Pattern.compile("^§5Guild Tomes: §f(\\d+)§7/(\\d+)$");
    private static final Pattern GUILD_REWARDS_ASPECTS_PATTERN =
            Pattern.compile("^§#d6401effAspects: §f(\\d+)§7/(\\d+)$");
    private static final Pattern GUILD_REWARDS_ITEM_NAME_PATTERN = Pattern.compile("§d§lGuild Rewards");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuildRaidCompleteEvent(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (event.getStyledText().contains("a")) {
            checkGuildRewards().thenAccept(rewardStorage -> {
                int emeralds = rewardStorage.getOrDefault(GuildRewardType.EMERALD, -1);
                int aspects = rewardStorage.getOrDefault(GuildRewardType.ASPECT, -1);
                int tomes = rewardStorage.getOrDefault(GuildRewardType.TOME, -1);
                McUtils.sendMessageToClient(Component.literal(
                        String.format("Emeralds : %d, Aspects : %d, Tomes : %d", emeralds, aspects, tomes)));
            });
        }
    }

    private CompletableFuture<Map<GuildRewardType, Integer>> checkGuildRewards() {
        CompletableFuture<Map<GuildRewardType, Integer>> future = new CompletableFuture<>();

        QueryBuilder queryBuilder = ScriptedContainerQuery.builder("Guild Reward Query");
        queryBuilder.onError(message -> {
            WynntilsMod.warn("Error querying guild rewards: " + message);
            future.completeExceptionally(new RuntimeException("Error querying guild rewards: " + message));
        });

        SequoiaMod.debug("Setting up query steps");
        queryBuilder
                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM)
                        .expectContainerTitle(ContainerModel.CHARACTER_INFO_NAME)
                        .processIncomingContainer(WynnUtils::parseCharacterContainerForGuildInfo))
                .conditionalThen(
                        content -> StringUtils.isNotBlank(Models.Guild.getGuildName()),
                        QueryStep.clickOnSlot(26).expectContainerTitle("[a-zA-Z\\s]+: Manage"))
                .then(QueryStep.clickOnSlot(0).expectContainerTitle("[a-zA-Z\\s]+: Members"))
                .then(QueryStep.clickOnSlot(28)
                        .expectContainerTitle("[a-zA-Z\\s]+: Members")
                        .processIncomingContainer(content -> {
                            Map<GuildRewardType, Integer> rewardStorage = getRewardsValue(content);
                            future.complete(rewardStorage);
                        }));

        queryBuilder.build().executeQuery();

        SequoiaMod.debug("Query setup complete");

        return future;
    }

    private Map<GuildRewardType, Integer> getRewardsValue(ContainerContent content) {
        SequoiaMod.debug("Verifying if enough emeralds are available for claim");
        Map<GuildRewardType, Integer> rewardStorage = new HashMap<>();
        ItemStack guildRewardsItem = content.items().get(GUILD_REWARDS_ITEM_SLOT);
        Matcher guildRewardsItemMatcher = StyledText.fromComponent(guildRewardsItem.getHoverName())
                .getNormalized()
                .getMatcher(GUILD_REWARDS_ITEM_NAME_PATTERN);
        if (!guildRewardsItemMatcher.matches()) {
            SequoiaMod.error("Could not parse guild rewards from item: " + LoreUtils.getLore(guildRewardsItem));
            return rewardStorage;
        }

        for (StyledText loreLine : LoreUtils.getLore(guildRewardsItem)) {
            SequoiaMod.debug("Item: " + loreLine);
            Matcher emeraldsMatcher = GUILD_REWARDS_EMERALDS_PATTERN.matcher(loreLine.getString());
            Matcher tomesMatcher = GUILD_REWARDS_TOMES_PATTERN.matcher(loreLine.getString());
            Matcher aspectsMatcher = GUILD_REWARDS_ASPECTS_PATTERN.matcher(loreLine.getString());
            if (emeraldsMatcher.matches()) {
                int emeralds = Integer.parseInt(emeraldsMatcher.group(1));
                SequoiaMod.debug("Emeralds found: " + emeralds);
                rewardStorage.put(GuildRewardType.EMERALD, emeralds);
            } else if (aspectsMatcher.matches()) {
                int aspects = Integer.parseInt(aspectsMatcher.group(1));
                SequoiaMod.debug("Aspects found: " + aspects);
                rewardStorage.put(GuildRewardType.ASPECT, aspects);
            } else if (tomesMatcher.matches()) {
                int tomes = Integer.parseInt(tomesMatcher.group(1));
                SequoiaMod.debug("Tomes found: " + tomes);
                rewardStorage.put(GuildRewardType.TOME, tomes);
            }
        }
        return rewardStorage;
    }

    public enum GuildRewardType {
        EMERALD,
        ASPECT,
        TOME
    }

    public boolean isEnabled() {
        return SequoiaMod.CONFIG.guildRewardStorageFullAlertFeature.enabled();
    }
}
