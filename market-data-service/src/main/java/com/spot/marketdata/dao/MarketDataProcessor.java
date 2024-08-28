package com.spot.marketdata.dao;

import com.spot.marketdata.model.MarketDepth;
import com.spot.marketdata.model.OHLC;
import com.spot.marketdata.model.Ticker;
import com.spot.marketdata.model.Trade;
import com.spot.marketdata.repository.MarketDepthRepository;
import com.spot.marketdata.repository.OHLCRepository;
import com.spot.marketdata.repository.TickerRepository;
import com.spot.marketdata.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MarketDataProcessor {

    private final MarketDepthRepository marketDepthRepository;
    private final OHLCRepository ohlcRepository;
    private final TradeRepository tradeRepository;
    private final TickerRepository tickerRepository;

    public MarketDataProcessor(MarketDepthRepository marketDepthRepository, OHLCRepository ohlcRepository,
                               TradeRepository tradeRepository, TickerRepository tickerRepository) {
        this.marketDepthRepository = marketDepthRepository;
        this.ohlcRepository = ohlcRepository;
        this.tradeRepository = tradeRepository;
        this.tickerRepository = tickerRepository;
    }

    public void saveOrUpdateMarketDepth(MarketDepth newMarketDepth) {
        Optional<MarketDepth> existingMarketDepth = marketDepthRepository.findBySymbol(newMarketDepth.getSymbol());

        if (existingMarketDepth.isPresent()) {
            MarketDepth updatedMarketDepth = existingMarketDepth.get();
            updatedMarketDepth.setAsks(newMarketDepth.getAsks());
            updatedMarketDepth.setBids(newMarketDepth.getBids());
            updatedMarketDepth.setTimestamp(newMarketDepth.getTimestamp());
            marketDepthRepository.save(updatedMarketDepth);
        } else {
            marketDepthRepository.save(newMarketDepth);
        }
    }

    public void saveOHLC(OHLC existingOHLC) {
        ohlcRepository.save(existingOHLC);
    }

    public void saveTrade(Trade trade) {
        tradeRepository.save(trade);
    }

    public void saveTicker(Ticker ticker) {
        tickerRepository.save(ticker);
    }
}
