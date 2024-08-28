package com.spot.websocket.dto.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TradeDto {

    private Data data;
    private String stream;

    @lombok.Setter
    public static class Data {
        private long eventTime; // Event time
        private long tradeTime; // Trade time
        private long sellerOrderId; // Seller order ID
        private long buyerOrderId; // Buyer order ID
        private String evenType; // Event type
        private boolean isBuyerMarketMaker; // Is the buyer the market maker?
        private String price; // Price
        private String quantity; // Quantity
        private String symbol; // Symbol
        private long tradeId; // Trade ID

        @JsonProperty("E")
        public long getEventTime() {
            return eventTime;
        }

        @JsonProperty("T")
        public long getTradeTime() {
            return tradeTime;
        }
        @JsonProperty("a")
        public long getSellerOrderId() {
            return sellerOrderId;
        }
        @JsonProperty("b")
        public long getBuyerOrderId() {
            return buyerOrderId;
        }

        @JsonProperty("e")
        public String getEvenType() {
            return evenType;
        }

        @JsonProperty("t")
        public long getTradeId() {
            return tradeId;
        }

        @JsonProperty("p")
        public String getPrice() {
            return price;
        }

        @JsonProperty("q")
        public String getQuantity() {
            return quantity;
        }

        @JsonProperty("m")
        public boolean isBuyerMarketMaker() {
            return isBuyerMarketMaker;
        }

        @JsonProperty("s")
        public String getSymbol() {
            return symbol;
        }
    }

}
