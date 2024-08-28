package com.spot.marketdata.dao;

import com.spot.marketdata.model.MarketDepth;
import com.spot.marketdata.model.OHLC;
import com.spot.marketdata.model.Ticker;
import com.spot.marketdata.model.Trade;
import com.spot.marketdata.repository.MarketDepthRepository;
import com.spot.marketdata.repository.OHLCRepository;
import com.spot.marketdata.repository.TickerRepository;
import com.spot.marketdata.repository.TradeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MarketDataReader {

    private final MarketDepthRepository marketDepthRepository;
    private final OHLCRepository ohlcRepository;
    private final TradeRepository tradeRepository;
    private final TickerRepository tickerRepository;

    public MarketDataReader(MarketDepthRepository marketDepthRepository, OHLCRepository ohlcRepository,
                            TradeRepository tradeRepository, TickerRepository tickerRepository) {
        this.marketDepthRepository = marketDepthRepository;
        this.ohlcRepository = ohlcRepository;
        this.tradeRepository = tradeRepository;
        this.tickerRepository = tickerRepository;
    }

    // Fetch Market Depth by ID
    public Optional<MarketDepth> findMarketDepthBySymbol(String symbol) {
        return marketDepthRepository.findBySymbol(symbol);
    }


    public OHLC getLastOHLCForYesterday(String symbol) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime startOfToday = now.toLocalDate().atStartOfDay(ZoneId.of("UTC"));
        long endOfYesterdayMillis = startOfToday.toInstant().toEpochMilli() - 1;

        return ohlcRepository.findTopBySymbolAndCloseTimeLessThanOrderByCloseTimeDesc(symbol, endOfYesterdayMillis)
                .orElse(null); // or handle this case as needed
    }

    public List<Trade> fetchTrades(String symbol, int limit) {
        return tradeRepository.findBySymbol(symbol, PageRequest.of(0, limit));
    }

    public Optional<Ticker> findTickerBySymbol(String symbol) {
        return tickerRepository.findBySymbol(symbol);
    }

    public Optional<OHLC> findLatestOHLCBySymbolAndInterval(String symbol, String interval) {
        return ohlcRepository.findLatestBySymbolAndInterval(symbol, interval);
    }
}
