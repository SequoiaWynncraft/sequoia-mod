/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.components;

import dev.lotnest.sequoia.handlers.GuildRaidHandler;

/**
 * Handlers span the bridge between Minecraft and Wynncraft. They manage a certain
 * aspect of Minecraft functionality, and with some Wynncraft knowledge, they distribute
 * the MC content to the actual Wynncraft models. A handler is needed when there is not a
 * clear 1-to-1 relationship between Minecraft components and models.
 * <p>
 * Handlers are created as singletons in the {@link Handlers} holding class.
 */
public abstract class Handler extends CoreComponent {
    public static final GuildRaidHandler GuildRaid = new GuildRaidHandler();

    protected Handler() {}

    @Override
    public String getTypeName() {
        return "Handler";
    }
}
