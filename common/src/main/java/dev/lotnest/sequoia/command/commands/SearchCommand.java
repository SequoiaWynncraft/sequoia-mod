/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.command.Command;
import dev.lotnest.sequoia.wynn.api.item.ItemResponse;
import dev.lotnest.sequoia.wynn.api.item.ItemService;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class SearchCommand extends Command {
    @Override
    public String getCommandName() {
        return "search";
    }

    @Override
    public List<String> getAliases() {
        return List.of("s");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(
            LiteralArgumentBuilder<CommandSourceStack> base) {
        return base.then(Commands.literal("item")
                        .then(Commands.argument("itemName", StringArgumentType.greedyString())
                                .executes(this::searchItem)))
                .executes(this::syntaxError);
    }

    private int searchItem(CommandContext<CommandSourceStack> context) {
        String itemName = context.getArgument("itemName", String.class);
        if (StringUtils.isBlank(itemName)) {
            context.getSource()
                    .sendFailure(SequoiaMod.prefix(Component.translatable("sequoia.command.search.invalidItemName")));
            return 0;
        }

        ItemService.searchItem(itemName).whenComplete((itemsResponse, throwable) -> {
            if (throwable != null) {
                SequoiaMod.error("Error searching item: " + itemName, throwable);
                context.getSource()
                        .sendFailure(SequoiaMod.prefix(
                                Component.translatable("sequoia.command.search.errorSearchingItem", itemName)));
                return;
            }

            if (itemsResponse == null
                    || itemsResponse.items() == null
                    || itemsResponse.items().isEmpty()) {
                context.getSource()
                        .sendFailure(SequoiaMod.prefix(
                                Component.translatable("sequoia.command.search.itemNotFound", itemName)));
                return;
            }

            if (itemsResponse.items().size() == 1) {
                Map.Entry<String, ItemResponse> itemResponseEntry =
                        itemsResponse.items().entrySet().iterator().next();
                String completedItemName = itemResponseEntry.getKey();
                ItemResponse itemResponse = itemResponseEntry.getValue();

                if (itemResponse != null) {
                    context.getSource()
                            .sendSuccess(
                                    () -> SequoiaMod.prefix(Component.translatable(
                                            "sequoia.command.search.itemFound", completedItemName, itemResponse)),
                                    false);
                } else {
                    context.getSource()
                            .sendFailure(SequoiaMod.prefix(
                                    Component.translatable("sequoia.command.search.itemNotFound", itemName)));
                }
            } else {
                context.getSource()
                        .sendSuccess(
                                () -> SequoiaMod.prefix(
                                        Component.translatable("sequoia.command.search.multipleItemsFound")
                                                .withStyle(ChatFormatting.YELLOW)),
                                false);

                itemsResponse.items().forEach((completedItemName, itemResponse) -> context.getSource()
                        .sendSystemMessage(SequoiaMod.prefix(
                                Component.literal(String.format("Item: %s - %s", completedItemName, itemResponse)))));
            }
        });

        context.getSource()
                .sendSystemMessage(
                        SequoiaMod.prefix(Component.translatable("sequoia.command.search.searchingItem", itemName)));
        return 1;
    }
}
