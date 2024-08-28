package com.spot.marketdata.controller;

import com.spot.marketdata.model.MarketDepth;
import com.spot.marketdata.model.OHLC;
import com.spot.marketdata.model.Trade;
import com.spot.marketdata.service.DBServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/market")
public class MarketDataController {

    private final DBServices dbServices;

    public MarketDataController(DBServices dbServices){
        this.dbServices = dbServices;
    }

    @GetMapping("/depth")
    public ResponseEntity<MarketDepth> getMarketDepth(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "100") int limit) {
        return dbServices.fetchMarketDepth(symbol, limit)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/trades")
    public ResponseEntity<List<Trade>> getTrades(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "500") int limit) {
        if (limit > 1000) limit = 1000; // Ensuring limit does not exceed 1000
        List<Trade> trades = dbServices.fetchTrades(symbol, limit);
        return ResponseEntity.ok(trades);
    }

    @GetMapping("/ticker")
    public ResponseEntity<?> getTicker(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) List<String> symbols) {
        if (symbol != null) {
            return dbServices.fetchTicker(symbol)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else if (symbols != null && !symbols.isEmpty()) {
            return ResponseEntity.ok(dbServices.fetchTickers(symbols));
        } else {
            return ResponseEntity.badRequest().body("Error: No valid symbol or symbols provided.");
        }
    }

    @GetMapping("/klines")
    public ResponseEntity<OHLC> getKlines(
            @RequestParam String symbol,
            @RequestParam String interval,
            @RequestParam(defaultValue = "500") int limit) {
        if (limit > 1000) limit = 1000; // Ensuring limit does not exceed 1000
        OHLC ohlcData = dbServices.fetchOHLCData(symbol, interval, limit);
        /*if (ohlcData == null) {
            return ResponseEntity.ok().body(null);
        }*/
        return ResponseEntity.ok(ohlcData);
    }
}
