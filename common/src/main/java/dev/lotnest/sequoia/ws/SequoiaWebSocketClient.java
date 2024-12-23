package dev.lotnest.sequoia.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.json.adapters.OffsetDateTimeAdapter;
import dev.lotnest.sequoia.manager.managers.AccessTokenManager;
import dev.lotnest.sequoia.ws.handlers.SChannelMessageHandler;
import dev.lotnest.sequoia.ws.handlers.SCommandPipeHandler;
import dev.lotnest.sequoia.ws.handlers.SMessageHandler;
import dev.lotnest.sequoia.ws.handlers.SSessionResultHandler;
import dev.lotnest.sequoia.ws.messages.session.GIdentifyWSMessage;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

public class SequoiaWebSocketClient extends WebSocketClient {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();
    public static final Pattern URL_PATTERN = Pattern.compile("(https?://\\S+)", Pattern.CASE_INSENSITIVE);
    public static final String WS_DEV_URL = "ws://localhost:8085/sequoia-tree/ws";
    public static final String WS_PROD_URL = "ws://lotnest.dev:8085/sequoia-tree/ws";

    private boolean isAuthenticating = false;

    public SequoiaWebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public String sendAsJson(Object object) {
        try {
            String json = GSON.toJson(object);
            SequoiaMod.debug("Sending WebSocket message: " + json);
            send(json);
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
        if (isAuthenticating()) {
            SequoiaMod.debug("Already authenticating with WebSocket server.");
            return;
        }

        SequoiaMod.debug("Authenticating with WebSocket server.");

        if (receivedInvalidTokenResult) {
            AccessTokenManager.invalidateAccessToken();
        }

        GIdentifyWSMessage gIdentifyWSMessage = new GIdentifyWSMessage(new GIdentifyWSMessage.Data(
                AccessTokenManager.retrieveAccessToken(), McUtils.player().getStringUUID()));
        sendAsJson(gIdentifyWSMessage);
    }

    public boolean isAuthenticating() {
        return isAuthenticating;
    }

    public void setAuthenticating(boolean isAuthenticating) {
        this.isAuthenticating = isAuthenticating;
    }

    public void connectIfNeeded() {
        if (SequoiaMod.getWebSocketClient().getReadyState() == ReadyState.NOT_YET_CONNECTED) {
            SequoiaMod.getWebSocketClient().connect();
        } else if (isClosed()) {
            SequoiaMod.getWebSocketClient().reconnect();
        }
    }

    public void closeIfNeeded() {
        if (isOpen()) {
            SequoiaMod.getWebSocketClient().close();
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        SequoiaMod.debug("WebSocket connection opened.");
        authenticate();
    }

    @Override
    public void onMessage(String s) {
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
        SequoiaMod.debug(
                "WebSocket connection closed. Code: " + i + (StringUtils.isNotBlank(s) ? ", Reason: " + s : ""));
        SequoiaMod.getWebSocketClient().setAuthenticating(false);
    }

    @Override
    public void onError(Exception e) {
        SequoiaMod.error("Error occurred in WebSocket connection", e);
    }
}
