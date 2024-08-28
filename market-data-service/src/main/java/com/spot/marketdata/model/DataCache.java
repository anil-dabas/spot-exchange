package com.spot.marketdata.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
public class DataCache {

    private Map<String, OHLC> prevClose = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, MarketDepth> depthCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<Trade>> tradeCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Ticker> tickerCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, OHLC>> ohlcDataMap = new ConcurrentHashMap<>();

    public void saveTrade(String symbol, Trade trade) {
        tradeCache.computeIfAbsent(symbol, k -> new ArrayList<>()).add(trade);
    }
}
