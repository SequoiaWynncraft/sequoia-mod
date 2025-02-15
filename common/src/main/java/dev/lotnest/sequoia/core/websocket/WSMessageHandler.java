/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.websocket;

public abstract class WSMessageHandler {
    protected final WSMessage wsMessage;
    protected String message;

    protected WSMessageHandler(WSMessage wsMessage, String message) {
        this.wsMessage = wsMessage;
        this.message = message;
    }

    public abstract void handle();
}
