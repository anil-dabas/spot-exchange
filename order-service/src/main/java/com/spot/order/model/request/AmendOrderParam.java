package com.spot.order.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmendOrderParam {
    private long orderId;
    private String instId;
    private String quantity;
    private String limitPrice;
    private LocalDateTime createdAt = LocalDateTime.now();
}
