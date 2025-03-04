/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.http.ratelimiter;

import dev.lotnest.sequoia.SequoiaMod;
import java.util.concurrent.Semaphore;

public class RateLimiter {
    private final Semaphore concurrencySemaphore;
    private final double capacity;
    private double tokens;
    private final double refillRate;
    private long lastRefillTime;

    public RateLimiter(int maxConcurrentRequests, double requestsPerMinute) {
        this.concurrencySemaphore = new Semaphore(maxConcurrentRequests);
        this.capacity = requestsPerMinute;
        this.tokens = requestsPerMinute;
        this.refillRate = requestsPerMinute / 60000.0;
        this.lastRefillTime = System.currentTimeMillis();
    }

    private void waitForToken() throws InterruptedException {
        while (true) {
            long sleepTime;
            synchronized (this) {
                refillTokens();
                if (tokens >= 1) {
                    tokens -= 1;
                    return;
                }
                double missingTokens = 1 - tokens;
                sleepTime = (long) Math.ceil(missingTokens / refillRate);
            }
            Thread.sleep(sleepTime);
        }
    }

    private synchronized void refillTokens() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        if (elapsed > 0) {
            tokens = Math.min(capacity, tokens + elapsed * refillRate);
            lastRefillTime = now;
        }
    }

    /**
     * Acquires permission for a request. This call blocks until both a token is available
     * (ensuring we respect the global rate) and a concurrency permit is available (ensuring
     * only a limited number of concurrent requests).
     */
    public void acquire() {
        try {
            waitForToken();
            concurrencySemaphore.acquire();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            SequoiaMod.warn("Interrupted while waiting for rate limiter", exception);
        }
    }

    /**
     * Releases a previously acquired concurrency permit.
     */
    public void release() {
        concurrencySemaphore.release();
    }
}
