package com.spot.websocket.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderDto {

    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private long eventTime;

    @JsonProperty("i")
    private String id;

    @JsonProperty("t")
    private String userId;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("o")
    private String type;

    @JsonProperty("S")
    private String side;

    @JsonProperty("p")
    private String price;

    @JsonProperty("q")
    private String quantity;

    @JsonProperty("Q")
    private String quoteQuantity;

    @JsonProperty("x")
    private String status;

    @JsonProperty("xq")
    private String executedQuantity;

    @JsonProperty("xQ")
    private String executedQuoteQuantity;

    @JsonProperty("O")
    private long createdAt;

    @JsonProperty("U")
    private long updatedAt;

}
