package com.spot.websocket.dto.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TickerDto {

    private Data data;
    private String stream;

    @lombok.Data
    public static class Data {

        @JsonProperty("E")
        private long eventTime; // Event time

        @JsonProperty("V")
        private String quoteVolume; // quote volume

        @JsonProperty("c")
        private String lastPrice; // close price - last price

        @JsonProperty("e")
        private String eventType; // event type

        @JsonProperty("h")
        private String high; // High price

        @JsonProperty("l")
        private String low; // low price

        @JsonProperty("n")
        private long trades; // total number of trades - trades

        @JsonProperty("o")
        private String firstPrice; // open price - first price

        @JsonProperty("s")
        private String symbol; // Symbol

        @JsonProperty("v")
        private String volume; //Volume - total trades base asset volume - volume

    }
}
