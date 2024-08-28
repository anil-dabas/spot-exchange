package com.spot.marketdata.service;

import com.spot.marketdata.dao.MarketDataProcessor;
import com.spot.marketdata.dao.MarketDataReader;
import com.spot.marketdata.model.*;
import com.spot.marketdata.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DBServices {

    private final MarketDataReader dataReader;
    private final MarketDataProcessor marketDataProcessor;
    private final DataCache dataCache;

    public DBServices(MarketDataReader dataReader, MarketDataProcessor marketDataProcessor, DataCache dataCache) {
        this.dataReader = dataReader;
        this.marketDataProcessor = marketDataProcessor;
        this.dataCache = dataCache;
    }

    public void saveOHLC(OHLC existingOHLC) {
        marketDataProcessor.saveOHLC(existingOHLC);
    }

    public void saveTrade(Trade trade) {
        marketDataProcessor.saveTrade(trade);
        dataCache.saveTrade(trade.getSymbol(), trade);
    }

    public double getLastClosePriceForYesterday(String symbol) {
        if (dataCache.getPrevClose().containsKey(symbol)) {
            return dataCache.getPrevClose().get(symbol).getClosePrice();
        }
        OHLC ohlc = dataReader.getLastOHLCForYesterday(symbol);
        if (ohlc != null) {
            dataCache.getPrevClose().put(symbol, ohlc);
        }
        return ohlc == null ? 0 : ohlc.getClosePrice();
    }

    public void saveMarketDepth(MarketDepth marketDepth) {
        marketDataProcessor.saveOrUpdateMarketDepth(marketDepth);
        dataCache.getDepthCache().put(marketDepth.getSymbol(), marketDepth);
    }

    public Optional<MarketDepth> fetchMarketDepth(String symbol, int limit) {
        return Optional.ofNullable(dataCache.getDepthCache().computeIfAbsent(symbol, s -> dataReader.findMarketDepthBySymbol(s).orElse(null)));
    }

    public List<Trade> fetchTrades(String symbol, int limit) {
        List<Trade> trades = dataCache.getTradeCache().getOrDefault(symbol, new ArrayList<>());
        if (trades.size() < limit) {
            trades = dataReader.fetchTrades(symbol, limit);
            dataCache.getTradeCache().put(symbol, trades);
        }
        return trades;
    }

    public void saveTicker(Ticker ticker) {
        marketDataProcessor.saveTicker(ticker);
        dataCache.getTickerCache().put(ticker.getSymbol(), ticker);
    }

    public Optional<Ticker> fetchTicker(String symbol) {
        return Optional.ofNullable(dataCache.getTickerCache().computeIfAbsent(symbol, s -> dataReader.findTickerBySymbol(s).orElse(null)));
    }

    public List<Ticker> fetchTickers(List<String> symbols) {
        List<Ticker> tickers = new ArrayList<>();
        for (String symbol : symbols) {
            fetchTicker(symbol).ifPresent(tickers::add);
        }
        return tickers;
    }

    public OHLC fetchOHLCData(String symbol, String interval, int limit) {
        // Use the ohlcDataMap cache to attempt to get the data
        Map<String, OHLC> ohlcData = dataCache.getOhlcDataMap().getOrDefault(symbol, new ConcurrentHashMap<>());
        OHLC ohlc = ohlcData.get(interval);
        if (ohlc != null) {
            log.debug("Retrieved OHLC data from cache for symbol: {} and interval: {}", symbol, interval);
            return ohlc; // Simplified for demonstration, handle actual limit and list logic
        } else {
            // Fetch from database if not in cache
            Optional<OHLC> ohlcFromDb = dataReader.findLatestOHLCBySymbolAndInterval(symbol, interval);
            if (ohlcFromDb.isPresent()) {
                ohlcData.put(interval, ohlcFromDb.get());
                dataCache.getOhlcDataMap().computeIfAbsent(symbol, k -> new ConcurrentHashMap<>());
                dataCache.getOhlcDataMap().get(symbol).put(interval, ohlcFromDb.get());
            }
            return ohlcFromDb.orElse(null);
        }
    }
}
