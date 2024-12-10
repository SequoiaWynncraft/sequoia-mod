package dev.lotnest.sequoia.ws;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynntils.utils.mc.McUtils;
import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.feature.features.discordchatbridge.SChannelMessageWSMessage;
import dev.lotnest.sequoia.json.adapters.OffsetDateTimeAdapter;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public final class SequoiaWebSocketClient extends WebSocketClient {
    private static final String WS_URL = "ws://64.225.107.161:8084/sequoia-tree/ws";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();
    private static final Cache<Long, String> FAILED_MESSAGES_CACHE =
            CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    private static final ConcurrentLinkedQueue<String> MESSAGE_QUEUE = new ConcurrentLinkedQueue<>();
    private static SequoiaWebSocketClient instance;
    private static long lastDisconnectionTime = 0;

    private SequoiaWebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public static synchronized SequoiaWebSocketClient getInstance() {
        if (instance == null || instance.isClosed()) {
            instance = new SequoiaWebSocketClient(
                    URI.create(WS_URL), Map.of("Authorization", "Bearer meowmeowAG6v92hc23LK5rqrSD278"));
            try {
                instance.connectBlocking();
            } catch (Exception exception) {
                SequoiaMod.error("Failed to connect to WebSocket server", exception);
            }
        }
        return instance;
    }

    public String sendAsJson(Object object) {
        try {
            if (instance.isClosed()) {
                SequoiaMod.debug("WebSocket is closed. Storing message for retry: " + object);
                storeFailedMessage(object);
                return null;
            }

            String payload = GSON.toJson(object);
            instance.send(payload);
            return payload;
        } catch (Exception exception) {
            SequoiaMod.error("Failed to send WebSocket message, storing for retry", exception);
            storeFailedMessage(object);
            return null;
        }
    }

    private void storeFailedMessage(Object object) {
        try {
            String payload = GSON.toJson(object);
            FAILED_MESSAGES_CACHE.put(System.currentTimeMillis(), payload);
            MESSAGE_QUEUE.add(payload);
        } catch (Exception exception) {
            SequoiaMod.error("Failed to serialize WebSocket message for storage", exception);
        }
    }

    private void resendFailedMessages() {
        while (!MESSAGE_QUEUE.isEmpty()) {
            String message = MESSAGE_QUEUE.poll();
            if (message != null) {
                instance.send(message);
            }
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        SequoiaMod.debug("Successfully connected to WebSocket server.");
        resendFailedMessages();
    }

    @Override
    public void onMessage(String message) {
        try {
            WSMessage wsMessage = GSON.fromJson(message, WSMessage.class);

            SequoiaMod.debug("Received WebSocket message: " + wsMessage);

            if (SequoiaMod.CONFIG.discordChatBridgeFeature.sendDiscordMessagesToInGameChat()) {
                if (wsMessage.getType() == WSMessageType.SChannelMessage.getValue()) {
                    SChannelMessageWSMessage.Data sChannelMessageWSMessageData =
                            GSON.fromJson(GSON.toJson(wsMessage.getData()), SChannelMessageWSMessage.Data.class);
                    SChannelMessageWSMessage sChannelMessageWSMessage =
                            new SChannelMessageWSMessage(sChannelMessageWSMessageData);

                    McUtils.sendMessageToClient(Component.literal("[DISCORD]")
                            .withStyle(style -> style.withColor(0x5865F2))
                            .append(Component.literal(" "
                                            + sChannelMessageWSMessage.getData().displayName() + " ➤ ")
                                    .withStyle(style -> style.withColor(0x5865F2)))
                            .append(Component.literal(
                                            sChannelMessageWSMessage.getData().message())
                                    .withStyle(ChatFormatting.GRAY)));
                }
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to parse WebSocket message: " + message, exception);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean isRemote) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDisconnectionTime > 60000) {
            lastDisconnectionTime = currentTime;

            SequoiaMod.debug("Disconnected from WebSocket server: " + reason);

            if (SequoiaMod.CONFIG.showWebSocketDisconnectMessages()) {
                McUtils.sendMessageToClient(SequoiaMod.prefix(
                        Component.literal("Disconnected from WebSocket server. Will try to reconnect in one minute.")
                                .withStyle(ChatFormatting.RED)));
            }
        }

        if (isRemote) {
            new Thread(() -> {
                        try {
                            Thread.sleep(60000);
                            instance = getInstance();
                        } catch (InterruptedException exception) {
                            SequoiaMod.error("Failed to reconnect to WebSocket server", exception);
                        }
                    })
                    .start();
        }
    }

    @Override
    public void onError(Exception exception) {
        SequoiaMod.error("WebSocket client encountered an error", exception);
    }
}
