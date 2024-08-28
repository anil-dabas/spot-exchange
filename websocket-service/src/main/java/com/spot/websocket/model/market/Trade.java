package com.spot.websocket.model.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {
    private long id;
    private String symbol;
    private long buy;
    private long sell;
    private String quantity;
    private String price;
    private boolean isBuyerMaker;
    private long timestamp;

    // Getter and setter for isBuyerMaker
    public boolean isBuyerMaker() {
        return isBuyerMaker;
    }
    @JsonProperty("is_buyer_maker")
    public void setBuyerMaker(boolean buyerMaker) {
        isBuyerMaker = buyerMaker;
    }
}
