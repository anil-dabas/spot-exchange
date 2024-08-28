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
public class CancelOrderParam {
    private long ordId;
    private String instId;
    private OrderType ordType;
    private LocalDateTime createdAt = LocalDateTime.now();
}
