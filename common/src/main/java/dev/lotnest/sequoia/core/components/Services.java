/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.components;

import dev.lotnest.sequoia.services.mojang.MojangService;
import dev.lotnest.sequoia.services.wynn.guild.GuildService;
import dev.lotnest.sequoia.services.wynn.item.ItemService;
import dev.lotnest.sequoia.services.wynn.player.PlayerService;

public final class Services {
    public static final PlayerService Player = new PlayerService();
    public static final GuildService Guild = new GuildService();
    public static final ItemService Item = new ItemService();
    public static final MojangService Mojang = new MojangService();

    private Services() {}
}
