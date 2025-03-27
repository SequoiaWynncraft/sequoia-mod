/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features.territory;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.models.guild.type.GuildRank;
import com.wynntils.models.territories.TerritoryInfo;
import com.wynntils.models.territories.type.GuildResource;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Models;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import dev.lotnest.sequoia.core.events.TerritoryCapturedEvent;
import dev.lotnest.sequoia.utils.wynn.WynnUtils;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;

public class TerritoryFeature extends Feature {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTerritoryCaptured(ChatMessageReceivedEvent event) {
        if (!isEnabled()) {
            return;
        }

        GuildRank guildRank = com.wynntils.core.components.Models.Guild.getGuildRank();
        if (guildRank == null || guildRank.ordinal() < GuildRank.STRATEGIST.ordinal()) {
            return;
        }

        StyledText resultMessage = event.getStyledText();
        String unformattedMessage =
                WynnUtils.getUnformattedString(event.getStyledText().getStringWithoutFormatting());
        Pair<String, String> territoryCapturerTerritoryNamePair =
                Models.Territory.parseTerritoryCapturedMessage(unformattedMessage);

        if (territoryCapturerTerritoryNamePair == null) {
            return;
        }

        String territoryCapturer = territoryCapturerTerritoryNamePair.getLeft();
        String territoryName = territoryCapturerTerritoryNamePair.getRight();
        TerritoryInfo territoryInfo = Models.Territory.getTerritoryInfo(territoryName);

        if (territoryInfo == null) {
            return;
        }

        WynntilsMod.postEvent(new TerritoryCapturedEvent(
                territoryCapturer, territoryName, territoryInfo.getGenerators(), territoryInfo.getTreasury()));

        if (SequoiaMod.CONFIG.territoryFeature.showCapturedTerritoryInfo()) {
            List<MutableComponent> generatorComponents = Lists.newArrayList();
            for (Map.Entry<GuildResource, Integer> generator :
                    territoryInfo.getGenerators().entrySet()) {
                GuildResource producedResource = generator.getKey();
                Integer producedAmount = generator.getValue();
                String prettySymbol = producedResource != GuildResource.EMERALDS
                        ? producedResource.getPrettySymbol()
                        : producedResource.getName();

                generatorComponents.add(Component.literal("- " + producedAmount + " " + prettySymbol)
                        .withStyle(producedResource.getColor()));
            }

            MutableComponent generatorsMessageParts = Component.literal("\n");
            for (int i = 0; i < generatorComponents.size(); i++) {
                generatorsMessageParts.append(generatorComponents.get(i));
                if (i < generatorComponents.size() - 1) {
                    generatorsMessageParts.append(Component.literal(", "));
                }
            }

            resultMessage = resultMessage
                    .append(StyledText.fromComponent(generatorsMessageParts))
                    .append(StyledText.fromString(" | "))
                    .append(StyledText.fromComponent(
                            Component.literal(territoryInfo.getTreasury().getAsString() + " treasury")
                                    .withStyle(territoryInfo.getTreasury().getTreasuryColor())))
                    .append(StyledText.fromString(" | "))
                    .append(StyledText.fromComponent(
                            Component.literal(territoryInfo.getDefences().getAsString() + " defences")
                                    .withStyle(territoryInfo.getDefences().getDefenceColor())));
        }

        event.setMessage(resultMessage.append(
                StyledText.fromComponent(Component.translatable("sequoia.feature.territory.clickToOpen")
                        .withStyle(style -> style.withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/seq territoryMenu"))))));
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.territoryFeature.enabled();
    }
}
