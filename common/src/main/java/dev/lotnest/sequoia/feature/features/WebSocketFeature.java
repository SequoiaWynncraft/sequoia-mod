/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.feature.features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynntils.core.components.Models;
import com.wynntils.models.character.event.CharacterUpdateEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.http.HttpUtils;
import dev.lotnest.sequoia.json.adapters.OffsetDateTimeAdapter;
import dev.lotnest.sequoia.manager.managers.AccessTokenManager;
import dev.lotnest.sequoia.upfixers.AccessTokenManagerUpfixer;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import dev.lotnest.sequoia.ws.handlers.SChannelMessageHandler;
import dev.lotnest.sequoia.ws.handlers.SCommandPipeHandler;
import dev.lotnest.sequoia.ws.handlers.SMessageHandler;
import dev.lotnest.sequoia.ws.handlers.SSessionResultHandler;
import dev.lotnest.sequoia.ws.messages.session.GIdentifyWSMessage;
import dev.lotnest.sequoia.wynn.WynnUtils;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketFeature extends Feature {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();
    private static final String WS_DEV_URL = "ws://localhost:8085/sequoia-tree/ws";
    private static final String WS_PROD_URL = "wss://lotnest.dev/sequoia-mod/ws";

    private WebSocketClient client;
    private boolean isFirstConnection = false;
    private boolean isAuthenticating;

    public void initClient() {
        if (McUtils.player() == null || StringUtils.isBlank(McUtils.player().getStringUUID())) {
            SequoiaMod.warn("Player UUID is not available. WebSocket connection will not be established.");
            return;
        }

        initClient(
                URI.create(
                        SequoiaMod.isDevelopmentEnvironment()
                                        || StringUtils.equalsIgnoreCase(System.getenv("SEQUOIA_MOD_USE_WS_DEV"), "TRUE")
                                ? WS_DEV_URL
                                : WS_PROD_URL),
                Map.of(
                        "Authoworization",
                        "Bearer meowmeowAG6v92hc23LK5rqrSD279",
                        "X-UUID",
                        McUtils.player().getStringUUID(),
                        "User-Agent",
                        HttpUtils.USER_AGENT));
    }

    private void initClient(URI serverUri, Map<String, String> httpHeaders) {
        if (client != null) {
            return;
        }

        client = new WebSocketClient(serverUri, httpHeaders) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                if (!isEnabled()) {
                    return;
                }

                SequoiaMod.debug("WebSocket connection opened.");
                authenticate();
            }

            @Override
            public void onMessage(String s) {
                if (!isEnabled()) {
                    return;
                }

                try {
                    WSMessage wsMessage = GSON.fromJson(s, WSMessage.class);
                    WSMessageType wsMessageType = WSMessageType.fromValue(wsMessage.getType());

                    SequoiaMod.debug("Received WebSocket message: " + wsMessage);

                    switch (wsMessageType) {
                        case S_CHANNEL_MESSAGE -> new SChannelMessageHandler(s).handle();
                        case S_SESSION_RESULT -> new SSessionResultHandler(s).handle();
                        case S_MESSAGE -> new SMessageHandler(s).handle();
                        case S_COMMAND_PIPE -> new SCommandPipeHandler(s).handle();
                        default -> SequoiaMod.debug("Unhandled WebSocket message type: " + wsMessageType);
                    }
                } catch (Exception exception) {
                    SequoiaMod.error("Failed to parse WebSocket message: " + s, exception);
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                if (!isEnabled()) {
                    return;
                }

                SequoiaMod.debug("WebSocket connection closed. Code: " + i
                        + (StringUtils.isNotBlank(s) ? ", Reason: " + s : ""));
                closeIfNeeded();
            }

            @Override
            public void onError(Exception e) {
                if (!isEnabled()) {
                    return;
                }

                SequoiaMod.error("Error occurred in WebSocket connection", e);
                setAuthenticating(false);
                if (StringUtils.equals(e.getMessage(), "java.net.ConnectException: Connection refused: connect")) {
                    client.close();
                }
            }
        };
    }

    public WebSocketClient getClient() {
        return client;
    }

    public String sendAsJson(Object object) {
        if (!isEnabled()) {
            return null;
        }

        if (client == null) {
            initClient();
        }

        if (!client.isOpen()) {
            return null;
        }

        try {
            String json = GSON.toJson(object);
            SequoiaMod.debug("Sending WebSocket message: " + json);
            client.send(json);
            return json;
        } catch (RuntimeException exception) {
            SequoiaMod.error("Failed to send WebSocket message", exception);
            return null;
        }
    }

    public void authenticate() {
        authenticate(false);
    }

    public void authenticate(boolean receivedInvalidTokenResult) {
        if (!isEnabled()) {
            return;
        }

        if (McUtils.player() == null || StringUtils.isBlank(McUtils.player().getStringUUID())) {
            SequoiaMod.warn("Player UUID is not available. WebSocket connection will not be established.");
            return;
        }

        if (!WynnUtils.isSequoiaGuildMember()) {
            return;
        }

        if (isAuthenticating()) {
            SequoiaMod.debug("Already authenticating with WebSocket server.");
            return;
        }

        SequoiaMod.debug("Authenticating with WebSocket server.");

        if (receivedInvalidTokenResult) {
            AccessTokenManager.invalidateAccessToken();
        }

        GIdentifyWSMessage gIdentifyWSMessage = new GIdentifyWSMessage(new GIdentifyWSMessage.Data(
                AccessTokenManager.retrieveAccessToken(), McUtils.player().getStringUUID(), SequoiaMod.getVersion()));
        sendAsJson(gIdentifyWSMessage);
    }

    public boolean isAuthenticating() {
        if (!isEnabled()) {
            return false;
        }
        return isAuthenticating;
    }

    public void setAuthenticating(boolean isAuthenticating) {
        if (!isEnabled()) {
            return;
        }
        this.isAuthenticating = isAuthenticating;
    }

    public void connectIfNeeded() {
        if (!isEnabled()) {
            return;
        }

        if (client == null) {
            initClient();
        }

        if (client.isOpen()) {
            return;
        }

        if (!isFirstConnection) {
            isFirstConnection = true;
            client.connect();
        } else {
            client.reconnect();
        }
    }

    public void closeIfNeeded() {
        if (client == null) {
            return;
        }

        if (client.isOpen()) {
            client.close();
        }
        setAuthenticating(false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCharacterUpdate(CharacterUpdateEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (!WynnUtils.isSequoiaGuildMember()) {
            return;
        }

        WebSocketFeature webSocketFeature = SequoiaMod.getWebSocketFeature();
        if (webSocketFeature == null || !webSocketFeature.isEnabled()) {
            return;
        }

        AccessTokenManagerUpfixer.fixLegacyFilesIfNeeded();

        try {
            webSocketFeature.initClient();
            webSocketFeature.connectIfNeeded();
        } catch (RuntimeException exception) {
            SequoiaMod.error("Failed to connect to WebSocket server: " + exception.getMessage());
        }
    }

    @Override
    public boolean isEnabled() {
        return SequoiaMod.CONFIG.webSocketFeature.enabled();
    }

    @Override
    public void onEnable() {
        if (!Models.WorldState.onWorld() && !Models.WorldState.onHousing()) {
            return;
        }

        initClient();
        connectIfNeeded();
    }

    @Override
    public void onDisable() {
        closeIfNeeded();
    }
}
