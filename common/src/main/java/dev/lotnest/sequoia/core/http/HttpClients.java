/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.http;

import dev.lotnest.sequoia.core.http.clients.MojangApiHttpClient;
import dev.lotnest.sequoia.core.http.clients.WynncraftApiHttpClient;

public final class HttpClients {
    public static final WynncraftApiHttpClient WYNNCRAFT_API = WynncraftApiHttpClient.newHttpClient();
    public static final MojangApiHttpClient MOJANG_API = MojangApiHttpClient.newHttpClient();

    private HttpClients() {}
}
