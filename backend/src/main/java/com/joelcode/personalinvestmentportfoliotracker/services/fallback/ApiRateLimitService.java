package com.joelcode.personalinvestmentportfoliotracker.services.fallback;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limit Tracking Service for External APIs
 *
 * Tracks API call counts and implements rate limiting to prevent
 * exceeding API quotas for FinnHub and MarketAux.
 *
 * FinnHub Free Tier: 60 API calls/minute
 * MarketAux Free Tier: 100 API calls/day
 */
@Service
public class ApiRateLimitService {

    // Track API call counts per minute for FinnHub
    private final Map<String, ApiCallTracker> finnhubCallTrackers = new ConcurrentHashMap<>();

    // Track daily call count for MarketAux
    private ApiCallTracker marketAuxDailyTracker = new ApiCallTracker(LocalDateTime.now(), 0);

    // Rate limit thresholds
    private static final int FINNHUB_MINUTE_LIMIT = 60;
    private static final int MARKETAUX_DAILY_LIMIT = 100;

    /**
     * Check if FinnHub API call can be made without exceeding rate limit
     * @return true if call is allowed, false if rate limit would be exceeded
     */
    public boolean canCallFinnhub() {
        String currentMinute = getCurrentMinuteKey();
        ApiCallTracker tracker = finnhubCallTrackers.computeIfAbsent(
            currentMinute,
            k -> new ApiCallTracker(LocalDateTime.now(), 0)
        );

        // Clean up old trackers (older than 2 minutes)
        cleanupOldFinnhubTrackers();

        return tracker.count < FINNHUB_MINUTE_LIMIT;
    }

    /**
     * Record a FinnHub API call
     */
    public void recordFinnhubCall() {
        String currentMinute = getCurrentMinuteKey();
        finnhubCallTrackers.computeIfPresent(currentMinute, (k, v) -> {
            v.count++;
            return v;
        });
    }

    /**
     * Check if MarketAux API call can be made without exceeding daily limit
     * @return true if call is allowed, false if rate limit would be exceeded
     */
    public boolean canCallMarketAux() {
        // Reset counter if it's a new day
        if (isDifferentDay(marketAuxDailyTracker.timestamp)) {
            marketAuxDailyTracker = new ApiCallTracker(LocalDateTime.now(), 0);
        }

        return marketAuxDailyTracker.count < MARKETAUX_DAILY_LIMIT;
    }

    /**
     * Record a MarketAux API call
     */
    public void recordMarketAuxCall() {
        marketAuxDailyTracker.count++;
    }

    /**
     * Get remaining FinnHub calls for current minute
     */
    public int getRemainingFinnhubCalls() {
        String currentMinute = getCurrentMinuteKey();
        ApiCallTracker tracker = finnhubCallTrackers.get(currentMinute);
        if (tracker == null) {
            return FINNHUB_MINUTE_LIMIT;
        }
        return Math.max(0, FINNHUB_MINUTE_LIMIT - tracker.count);
    }

    /**
     * Get remaining MarketAux calls for current day
     */
    public int getRemainingMarketAuxCalls() {
        if (isDifferentDay(marketAuxDailyTracker.timestamp)) {
            return MARKETAUX_DAILY_LIMIT;
        }
        return Math.max(0, MARKETAUX_DAILY_LIMIT - marketAuxDailyTracker.count);
    }

    private String getCurrentMinuteKey() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%d-%02d-%02d_%02d:%02d",
            now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
            now.getHour(), now.getMinute());
    }

    private void cleanupOldFinnhubTrackers() {
        LocalDateTime twoMinutesAgo = LocalDateTime.now().minus(Duration.ofMinutes(2));
        finnhubCallTrackers.entrySet().removeIf(entry ->
            entry.getValue().timestamp.isBefore(twoMinutesAgo)
        );
    }

    private boolean isDifferentDay(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        return !timestamp.toLocalDate().equals(now.toLocalDate());
    }

    /**
     * Simple tracker for API calls with timestamp
     */
    private static class ApiCallTracker {
        LocalDateTime timestamp;
        int count;

        ApiCallTracker(LocalDateTime timestamp, int count) {
            this.timestamp = timestamp;
            this.count = count;
        }
    }
}
