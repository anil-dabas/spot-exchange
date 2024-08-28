package com.spot.order.model.request;

import com.spot.order.model.domain.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderParam {

    private String instId;
    private int side;
    private OrderType ordType;
    private String quantity;
    // Only for limit orders
    private String limitPrice;
    private LocalDateTime createdAt = LocalDateTime.now();

}
