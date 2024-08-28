package com.spot.marketdata.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "ohlc")
public class OHLC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    @Column(name = "`interval`")  // Escape the reserved word
    private String interval;
    private long openTime;
    @Setter
    private long closeTime;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double volume;
    private double quoteAssetVolume;
    private int numberOfTrades;
    private double takerBuyBaseAssetVolume;
    private double takerBuyQuoteAssetVolume;


    public OHLC(String symbol, String interval,long openTime, long closeTime) {
        this.symbol = symbol;
        this.interval = interval;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.highPrice = Double.MIN_VALUE;
        this.lowPrice = Double.MAX_VALUE;
    }

    public OHLC(String symbol, String interval,long openTime, long closeTime, double closePrice) {
        this.symbol = symbol;
        this.interval = interval;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.highPrice = closePrice;
        this.lowPrice = closePrice;
        this.openPrice = closePrice;
        this.closePrice = closePrice;
    }

    public synchronized void update(double price, double quantity) {
        if (openPrice == 0) openPrice = price;
        closePrice = price;
        if (price > highPrice) highPrice = price;
        if (price < lowPrice) lowPrice = price;
        volume += quantity;
        quoteAssetVolume += price * quantity;
        numberOfTrades++;
        takerBuyBaseAssetVolume += quantity;
        takerBuyQuoteAssetVolume += price * quantity;
    }

}
