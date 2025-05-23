/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws.handler.ws;

import static dev.lotnest.sequoia.core.ws.WSConstants.GSON;

import com.google.gson.JsonElement;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.core.ws.handler.WSMessageHandler;
import dev.lotnest.sequoia.core.ws.message.ws.SMessageWSMessage;
import dev.lotnest.sequoia.utils.URLUtils;
import java.util.regex.Matcher;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;

public class SMessageWSMessageHandler extends WSMessageHandler {
    public SMessageWSMessageHandler(String message) {
        super(GSON.fromJson(message, SMessageWSMessage.class), message);
    }

    @Override
    public void handle() {
        SMessageWSMessage sMessageWSMessage = (SMessageWSMessage) wsMessage;
        JsonElement sMessageWSMessageData = sMessageWSMessage.getData();

        if (sMessageWSMessageData.isJsonPrimitive()) {
            String serverMessageText = sMessageWSMessageData.getAsString();
            if (StringUtils.equals(serverMessageText, "Authentication required.")) {
                SequoiaMod.debug("Received authentication required message, reauthenticating.");
                SequoiaMod.getWebSocketFeature().authenticate();
                return;
            }

            Matcher matcher = URLUtils.getURLMatcher(serverMessageText);
            MutableComponent messageComponent = Component.literal("Server message ➤ ");
            int lastMatchEnd = 0;

            while (matcher.find()) {
                if (matcher.start() > lastMatchEnd) {
                    messageComponent = messageComponent.append(
                            Component.literal(serverMessageText.substring(lastMatchEnd, matcher.start()))
                                    .withStyle(style -> style.withColor(0x19A775)));
                }

                String url = matcher.group();
                messageComponent =
                        messageComponent.append(Component.literal(url).withStyle(style -> style.withColor(0x1DA1F2)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                .withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open URL")))));

                lastMatchEnd = matcher.end();
            }

            if (lastMatchEnd < serverMessageText.length()) {
                messageComponent = messageComponent.append(Component.literal(serverMessageText.substring(lastMatchEnd))
                        .withStyle(style -> style.withColor(0x19A775)));
            }

            McUtils.sendMessageToClient(SequoiaMod.prefix(messageComponent));
        } else {
            McUtils.sendMessageToClient(
                    SequoiaMod.prefix(Component.literal("Server message ➤ " + sMessageWSMessageData))
                            .withStyle(style -> style.withColor(0x19A775)));
        }
    }
}
