package com.spot.websocket.util;

import org.apache.commons.lang3.RandomStringUtils;

public class Util {

    private static final int LISTEN_KEY_LENGTH = 64;

    public static String generateListKey() {
        return RandomStringUtils.randomAlphanumeric(LISTEN_KEY_LENGTH);
    }

    public static String[] parseTradePair(String tradePairStr) {
        if (tradePairStr == null || !tradePairStr.contains("@")) {
            return null;
        }
        String[] parts = tradePairStr.split("@");
        if (parts.length != 2) {
            return null;
        }
        return parts;
    }

    // channel format trades@BTC_USDT: op@pairs
    // topic market.BTC_USDT.trades
    public static String channel2Topic(String channel) {
        if (channel == null || !channel.contains("@")) {
            return null;
        }
        String[] parts = channel.split("@");
        if (parts.length != 2) {
            return null;
        }
        // process
        return String.format("market.%s.%s", parts[1], parts[0]);
    }

    public static String topic2Channel(String topic) {
        if (topic == null || !topic.contains(".")) {
            return null;
        }
        String[] parts = topic.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        // process
        return String.format("%s@%s", parts[2], parts[1]);
    }
}
