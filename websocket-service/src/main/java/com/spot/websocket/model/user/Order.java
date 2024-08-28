package com.spot.websocket.model.user;
//{
//        "id": 2072271355033796608,
//        "user_id":"sdfsdfdsf",
//        "id_str": "2072271355033796608",
//        "created_at": 1720086389349268000,
//        "updated_at": 1720086389349268000,
//        "symbol": "BTC_USDT",
//        "type": "LIMIT",
//        "side": "BUY",
//        "price": "50",
//        "quantity": "1",
//        "quote_quantity": "0",
//        "status": "NEW",
//        "executed_quantity": "0",
//        "executed_quote_quantity": "0"
//        }

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    private String id;
    @JsonProperty("user_id")
    private String userId;
    private String symbol;
    private String type;
    private String side;
    private String price;
    private String quantity;
    @JsonProperty("quote_quantity")
    private String quoteQuantity;
    private String status;

    @JsonProperty("executed_quantity")
    private String executedQuantity;

    @JsonProperty("executed_quote_quantity")
    private String executedQuoteQuantity;

    @JsonProperty("created_at")
    private long createdAt;

    @JsonProperty("updated_at")
    private long updatedAt;


}
