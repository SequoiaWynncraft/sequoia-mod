/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.features;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.wynntils.core.consumers.features.properties.RegisterCommand;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.components.Managers;
import dev.lotnest.sequoia.core.consumers.features.Feature;
import java.lang.reflect.Field;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.commons.lang3.reflect.FieldUtils;

public class FeatureCommands {
    private final LiteralArgumentBuilder<CommandSourceStack> commandNodeBuilder;
    private LiteralCommandNode<CommandSourceStack> commandNode;

    public FeatureCommands() {
        commandNodeBuilder = Commands.literal("execute");
    }

    public void discoverCommands(Feature feature) {
        for (Field field : FieldUtils.getFieldsWithAnnotation(feature.getClass(), RegisterCommand.class)) {
            if (!field.getType().equals(LiteralCommandNode.class)) {
                SequoiaMod.error("Incorrect type for @RegisterCommand " + field.getName() + " in "
                        + feature.getClass().getName());
                return;
            }

            try {
                LiteralCommandNode<CommandSourceStack> node =
                        (LiteralCommandNode<CommandSourceStack>) FieldUtils.readField(field, feature, true);

                LiteralCommandNode<CommandSourceStack> featureNode =
                        Commands.literal(feature.getShortName()).build();
                featureNode.addChild(node);
                commandNodeBuilder.then(featureNode);
            } catch (IllegalAccessException exception) {
                SequoiaMod.error(
                        "Failed reading field of @RegisterCommand " + field.getName() + " in "
                                + feature.getClass().getName(),
                        exception);
            }
        }
    }

    public void init() {
        if (commandNode == null) {
            commandNode = commandNodeBuilder.build();
        }

        Managers.Command.addNodeToClientDispatcher(commandNodeBuilder);
    }

    public LiteralCommandNode<CommandSourceStack> getCommandNode() {
        assert commandNode != null;
        return commandNode;
    }
}
