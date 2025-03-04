/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.http.ratelimiter;

public final class RateLimiters {
    public static final RateLimiter WYNNCRAFT_API = new RateLimiter(5, 180);
    public static final RateLimiter MOJANG_API = new RateLimiter(5, 60);

    private RateLimiters() {}
}
