/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lotnest.sequoia.core.json.OffsetDateTimeAdapter;
import java.time.OffsetDateTime;

public final class WSConstants {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();

    private WSConstants() {}
}
