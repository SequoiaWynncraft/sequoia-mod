package dev.lotnest.sequoia.ws;

public abstract class WSMessageHandler {
    protected final WSMessage wsMessage;
    protected String message;

    protected WSMessageHandler(WSMessage wsMessage, String message) {
        this.wsMessage = wsMessage;
        this.message = message;
    }

    public abstract void handle();
}
