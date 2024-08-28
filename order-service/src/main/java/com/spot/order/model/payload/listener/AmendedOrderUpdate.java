package com.spot.order.model.payload.listener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendedOrderUpdate {
    private Long id;
    private long orderId;
    private int side;
    private String newQuantity;
    private String remainQuantity;
    private String remainQuoteQuantity;
}
