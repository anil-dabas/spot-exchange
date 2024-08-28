package com.spot.marketdata.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TimeIntervals {
    @Getter
    private static final Map<String, Long> intervals = new HashMap<>();

    static {
        // Seconds
        intervals.put("1s", 1L);

        // Minutes
        intervals.put("1m", 60L);
        intervals.put("3m", 180L);
        intervals.put("5m", 300L);
        intervals.put("15m", 900L);
        intervals.put("30m", 1800L);

        // Hours
        intervals.put("1h", 3600L);
        intervals.put("2h", 7200L);
        intervals.put("4h", 14400L);
        intervals.put("6h", 21600L);
        intervals.put("8h", 28800L);
        intervals.put("12h", 43200L);

        // Days
        intervals.put("1d", 86400L);
        intervals.put("3d", 259200L);

        // Weeks
        intervals.put("1w", 604800L);

        // Months (approximated as 30 days for simplicity)
        intervals.put("1M", 2592000L);
    }
}