package com.spot.marketdata.model;

public enum TimeIntervalEnum {
    ONE_SECOND("1s", 1),
    ONE_MINUTE("1m", 60),
    THREE_MINUTES("3m", 180),
    FIVE_MINUTES("5m", 300),
    FIFTEEN_MINUTES("15m", 900),
    THIRTY_MINUTES("30m", 1800),
    ONE_HOUR("1h", 3600),
    TWO_HOURS("2h", 7200),
    FOUR_HOURS("4h", 14400),
    SIX_HOURS("6h", 21600),
    EIGHT_HOURS("8h", 28800),
    TWELVE_HOURS("12h", 43200),
    ONE_DAY("1d", 86400),
    THREE_DAYS("3d", 259200),
    ONE_WEEK("1w", 604800),
    ONE_MONTH("1M", 2592000);

    private final String key;
    private final long seconds;

    TimeIntervalEnum(String key, long seconds) {
        this.key = key;
        this.seconds = seconds;
    }

    public String getKey() {
        return key;
    }

    public long getSeconds() {
        return seconds;
    }

    public static TimeIntervalEnum fromKey(String key) {
        for (TimeIntervalEnum interval : values()) {
            if (interval.getKey().equals(key)) {
                return interval;
            }
        }
        throw new IllegalArgumentException("Unsupported interval: " + key);
    }
}

