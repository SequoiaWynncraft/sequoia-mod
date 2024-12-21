package dev.lotnest.sequoia.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wynntils.core.mod.event.WynncraftConnectionEvent;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.features.discordchatbridge.SChannelMessageWSMessage;
import dev.lotnest.sequoia.json.adapters.OffsetDateTimeAdapter;
import dev.lotnest.sequoia.manager.managers.AccessTokenManager;
import dev.lotnest.sequoia.ws.messages.SMessageWSMessage;
import dev.lotnest.sequoia.ws.messages.session.GIdentifyWSMessage;
import dev.lotnest.sequoia.ws.messages.session.SSessionResultWSMessage;
import dev.lotnest.sequoia.wynn.guild.GuildService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

public final class SequoiaWebSocketClient extends WebSocketClient {
    private static final String WS_DEV_URL = "ws://localhost:8085/sequoia-tree/ws";
    private static final String WS_PROD_URL = "ws://lotnest.dev:8085/sequoia-tree/ws";
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://\\S+)", Pattern.CASE_INSENSITIVE);
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();
    private static final ConcurrentLinkedQueue<String> MESSAGE_QUEUE = new ConcurrentLinkedQueue<>();
    private static final Lock connectionLock = new ReentrantLock();
    private static boolean isAuthenticationPending = false;

    private SequoiaWebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    private static void init() {
        connectionLock.lock();
        try {
            SequoiaWebSocketClient client = getInstance();
            if (client != null && client.getReadyState() == ReadyState.NOT_YET_CONNECTED) {
                client.connect();
                client.authenticate(false);
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to initialize WebSocket client", exception);
        } finally {
            connectionLock.unlock();
        }
    }

    private static final class InstanceHolder {
        private static final SequoiaWebSocketClient instance = new SequoiaWebSocketClient(
                URI.create(SequoiaMod.isDevelopmentEnvironment() ? WS_DEV_URL : WS_PROD_URL),
                Map.of(
                        "Authoworization",
                        "Bearer meowmeowAG6v92hc23LK5rqrSD279",
                        "X-UUID",
                        McUtils.player().getStringUUID()));
    }

    public static SequoiaWebSocketClient getInstance() {
        connectionLock.lock();
        try {
            if (!GuildService.isSequoiaGuildMember()) {
                return null;
            }

            SequoiaWebSocketClient instance = InstanceHolder.instance;
            if (!instance.isOpen()) {
                CompletableFuture.runAsync(instance::connect);
            }
            return instance;
        } finally {
            connectionLock.unlock();
        }
    }

    public String sendAsJson(Object object) {
        try {
            if (InstanceHolder.instance.isClosed()) {
                return null;
            }
            String payload = GSON.toJson(object);
            InstanceHolder.instance.send(payload);
            SequoiaMod.debug("Sent WebSocket message: " + payload);
            return payload;
        } catch (WebsocketNotConnectedException ignored) {
        } catch (Exception exception) {
            SequoiaMod.error("Unexpected error while sending WebSocket message", exception);
        }
        return null;
    }

    private void authenticate(boolean isInvalidToken) {
        connectionLock.lock();
        try {
            if (isAuthenticationPending) {
                return;
            }

            if (isInvalidToken) {
                AccessTokenManager.invalidateAccessToken();
            }

            GIdentifyWSMessage gIdentifyWSMessage = new GIdentifyWSMessage(new GIdentifyWSMessage.Data(
                    AccessTokenManager.retrieveAccessToken(), McUtils.player().getStringUUID()));
            CompletableFuture.runAsync(() -> sendAsJson(gIdentifyWSMessage)).exceptionally(exception -> {
                SequoiaMod.error("Failed to send GIdentify request", exception);
                return null;
            });
        } finally {
            connectionLock.unlock();
        }
    }

    public boolean isIsAuthenticationPending() {
        return isAuthenticationPending;
    }

    private void resendFailedMessages() {
        if (isAuthenticationPending) {
            return;
        }

        while (!MESSAGE_QUEUE.isEmpty()) {
            String message = MESSAGE_QUEUE.poll();
            if (StringUtils.isNotBlank(message)) {
                CompletableFuture.runAsync(() -> {
                            InstanceHolder.instance.send(message);
                            SequoiaMod.debug("Resent failed message: " + message);
                        })
                        .exceptionally(exception -> {
                            SequoiaMod.error("Failed to resend message: " + message, exception);
                            return null;
                        });
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWynncraftConnected(WynncraftConnectionEvent.Connected event) {
        init();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWynncraftDisconnected(WynncraftConnectionEvent.Disconnected event) {
        if (InstanceHolder.instance != null && InstanceHolder.instance.isOpen()) {
            InstanceHolder.instance.close();
        }
        isAuthenticationPending = false;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        SequoiaMod.debug("Received handshake from WebSocket server.");
        resendFailedMessages();
    }

    @Override
    public void onMessage(String message) {
        try {
            WSMessage wsMessage = GSON.fromJson(message, WSMessage.class);
            WSMessageType wsMessageType = WSMessageType.fromValue(wsMessage.getType());

            SequoiaMod.debug("Received WebSocket message: " + wsMessage);

            switch (wsMessageType) {
                case SChannelMessage -> {
                    if (SequoiaMod.CONFIG.discordChatBridgeFeature.sendDiscordMessagesToInGameChat()) {
                        SChannelMessageWSMessage sChannelMessageWSMessage =
                                GSON.fromJson(message, SChannelMessageWSMessage.class);
                        SChannelMessageWSMessage.Data sChannelMessageWSMessageData =
                                sChannelMessageWSMessage.getChannelMessageData();

                        McUtils.sendMessageToClient(
                                Component.literal("[DISCORD] " + sChannelMessageWSMessageData.displayName() + " ➤ "
                                                + sChannelMessageWSMessageData.message())
                                        .withStyle(style -> style.withColor(0x5865F2)));
                    }
                }
                case SSessionResult -> {
                    SSessionResultWSMessage sSessionResultWSMessage =
                            GSON.fromJson(message, SSessionResultWSMessage.class);
                    SSessionResultWSMessage.Data sSessionResultWSMessageData =
                            sSessionResultWSMessage.getSessionResultData();

                    if (StringUtils.equals(sSessionResultWSMessageData.result(), "Authentication pending.")) {
                        SequoiaMod.debug("Authentication pending, waiting for successful authentication.");
                        return;
                    }

                    if (!sSessionResultWSMessageData.error()) {
                        isAuthenticationPending = false;
                        if (!StringUtils.equals(
                                AccessTokenManager.retrieveAccessToken(), sSessionResultWSMessageData.result())) {
                            AccessTokenManager.storeAccessToken(sSessionResultWSMessageData.result());
                        }
                    } else {
                        SequoiaMod.error("Failed to authenticate with WebSocket server: "
                                + sSessionResultWSMessageData.result());
                    }
                }
                case SMessage -> {
                    SMessageWSMessage sMessageWSMessage = GSON.fromJson(message, SMessageWSMessage.class);
                    JsonElement sMessageWSMessageData = sMessageWSMessage.getData();

                    if (sMessageWSMessageData.isJsonPrimitive()) {
                        String serverMessageText = sMessageWSMessageData.getAsString();
                        Matcher matcher = URL_PATTERN.matcher(serverMessageText);
                        MutableComponent messageComponent = Component.literal("Server message ➤ ");
                        int lastMatchEnd = 0;

                        while (matcher.find()) {
                            if (matcher.start() > lastMatchEnd) {
                                messageComponent = messageComponent.append(
                                        Component.literal(serverMessageText.substring(lastMatchEnd, matcher.start()))
                                                .withStyle(style -> style.withColor(0x19A775)));
                            }

                            String url = matcher.group();
                            messageComponent = messageComponent.append(
                                    Component.literal(url).withStyle(style -> style.withColor(0x1DA1F2)
                                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                            .withHoverEvent(new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT,
                                                    Component.literal("Click to open URL")))));

                            lastMatchEnd = matcher.end();
                        }

                        if (lastMatchEnd < serverMessageText.length()) {
                            messageComponent =
                                    messageComponent.append(Component.literal(serverMessageText.substring(lastMatchEnd))
                                            .withStyle(style -> style.withColor(0x19A775)));
                        }

                        McUtils.sendMessageToClient(SequoiaMod.prefix(messageComponent));
                    } else {
                        McUtils.sendMessageToClient(
                                SequoiaMod.prefix(Component.literal("Server message ➤ " + sMessageWSMessageData))
                                        .withStyle(style -> style.withColor(0x19A775)));
                    }
                }
                case SCommandPipe -> {
                    if (StringUtils.equals("Invalid token", wsMessage.getData().getAsString())) {
                        SequoiaMod.debug("Received invalid token response. Requesting a new token.");
                        authenticate(true);
                    }
                }
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to parse WebSocket message: " + message, exception);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        SequoiaMod.debug("WebSocket connection closed. Code: " + code
                + (StringUtils.isNotBlank(reason) ? ", Reason: " + reason : ""));
    }

    @Override
    public void onError(Exception exception) {
        SequoiaMod.error("WebSocket error: ", exception);
    }
}
