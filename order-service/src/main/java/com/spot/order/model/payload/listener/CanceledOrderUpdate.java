package com.spot.order.model.payload.listener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanceledOrderUpdate {
    private String id;
    private long orderId;
    private String side;
    private String remainQuantity;
    private String remainQuoteQuantity;
    private String newQuantity;
}
