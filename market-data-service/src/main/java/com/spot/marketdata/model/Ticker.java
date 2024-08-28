package com.spot.marketdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "first_price")
    private double firstPrice;

    @Column(nullable = false)
    private double high;

    @Column(name = "last_price")
    private double lastPrice;

    @Column(nullable = false)
    private double low;

    @Column(name = "price_change")
    private double priceChange;

    @Column(name = "price_change_percent")
    private double priceChangePercent;

    @Column(name = "quote_volume")
    private double quoteVolume;

    @Column(nullable = false)
    private int trades;

    @Column(nullable = false)
    private double volume;

    public Ticker(String symbol, double firstPrice, double high, double lastPrice, double low, double priceChange, double priceChangePercent, double quoteVolume, int trades, double volume) {
        this.symbol = symbol;
        this.firstPrice = firstPrice;
        this.high = high;
        this.lastPrice = lastPrice;
        this.low = low;
        this.priceChange = priceChange;
        this.priceChangePercent = priceChangePercent;
        this.quoteVolume = quoteVolume;
        this.trades = trades;
        this.volume = volume;
    }
}

