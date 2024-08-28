package com.spot.order.client.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


@Service
public class RedisMarketDataClient {

    @Value("${marketdata.redis.key-pattern:matching:{symbol}:last_matched_price}")
    private String keyPattern;

    private final RedisTemplate<String, String> redisTemplate;


    public RedisMarketDataClient(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;

    }

    public double getLatestInstrumentData(String symbol) {
        String key = keyPattern.replace("{symbol}", symbol);
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Failed to parse market data for symbol: " + symbol, e);
            }
        }
        throw new IllegalStateException("No data available for symbol: " + symbol);
    }
}
