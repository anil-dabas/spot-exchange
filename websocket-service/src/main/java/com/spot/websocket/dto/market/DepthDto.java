package com.spot.websocket.dto.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DepthDto {

    private Data data;
    private String stream;

    @lombok.Setter
    public static class Data {
        private long eventTime; // Event time
        private long tradeTime; // Trade time
        private List<List<String>> asks; // Asks
        private List<List<String>> bids; // Bids
        private String evenType; // Event type
        private String price; // Price
        private String symbol; // Symbol

        @JsonProperty("E")
        public long getEventTime() {
            return eventTime;
        }
        @JsonProperty("T")
        public long getTradeTime() {
            return tradeTime;
        }

        @JsonProperty("a")
        public List<List<String>> getAsks() {
            return asks;
        }

        @JsonProperty("b")
        public List<List<String>> getBids() {
            return bids;
        }

        @JsonProperty("e")
        public String getEvenType() {
            return evenType;
        }

        @JsonProperty("s")
        public String getSymbol() {
            return symbol;
        }
    }
}
