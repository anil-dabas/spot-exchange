package com.spot.websocket.model.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {

    @JsonProperty("first_price")
    private String firstPrice;

    @JsonProperty("high")
    private String high;

    @JsonProperty("last_price")
    private String lastPrice;

    @JsonProperty("low")
    private String low;

    @JsonProperty("price_change")
    private String priceChange;

    @JsonProperty("price_change_percent")
    private String priceChangePercent;

    @JsonProperty("quote_volume")
    private String quoteVolume;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("trades")
    private String trades;

    @JsonProperty("volume")
    private String volume;

}
