package dev.lotnest.sequoia.feature.features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynntils.core.components.Models;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.Feature;
import dev.lotnest.sequoia.json.adapters.OffsetDateTimeAdapter;
import dev.lotnest.sequoia.manager.managers.AccessTokenManager;
import dev.lotnest.sequoia.ws.WSMessage;
import dev.lotnest.sequoia.ws.WSMessageType;
import dev.lotnest.sequoia.ws.handlers.SChannelMessageHandler;
import dev.lotnest.sequoia.ws.handlers.SCommandPipeHandler;
import dev.lotnest.sequoia.ws.handlers.SMessageHandler;
import dev.lotnest.sequoia.ws.handlers.SSessionResultHandler;
import dev.lotnest.sequoia.ws.messages.session.GIdentifyWSMessage;
import dev.lotnest.sequoia.wynn.api.guild.GuildService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketFeature extends Feature {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();
    public static final Pattern URL_PATTERN = Pattern.compile("(https?://\\S+)", Pattern.CASE_INSENSITIVE);
    public static final String WS_DEV_URL = "ws://localhost:8085/sequoia-tree/ws";
    public static final String WS_PROD_URL = "ws://lotnest.dev:8085/sequoia-tree/ws";

    private WebSocketClient client;
    private boolean isAuthenticating;

    public void initClient() {
        initClient(
                URI.create(SequoiaMod.isDevelopmentEnvironment() ? WS_DEV_URL : WS_PROD_URL),
                Map.of(
                        "Authoworization",
                        "Bearer meowmeowAG6v92hc23LK5rqrSD279",
                        "X-UUID",
                        McUtils.player().getStringUUID()));
    }

    public void initClient(URI serverUri, Map<String, String> httpHeaders) {
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
                        case SChannelMessage -> new SChannelMessageHandler(s).handle();
                        case SSessionResult -> new SSessionResultHandler(s).handle();
                        case SMessage -> new SMessageHandler(s).handle();
                        case SCommandPipe -> new SCommandPipeHandler(s).handle();
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
                setAuthenticating(false);
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

        try {
            String json = GSON.toJson(object);
            SequoiaMod.debug("Sending WebSocket message: " + json);
            client.send(json);
            return json;
        } catch (Exception exception) {
            SequoiaMod.error("Failed to send WebSocket message: " + exception.getMessage());
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

        if (!GuildService.isSequoiaGuildMember()) {
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

        if (client.getReadyState() == ReadyState.NOT_YET_CONNECTED) {
            client.connect();
        } else if (client.isClosed()) {
            client.reconnect();
        }
    }

    public void closeIfNeeded() {
        if (client.isOpen()) {
            client.close();
        }
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
