package com.spot.order.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "order_request")
@Table(name = "ex_order_request")
public class OrderRequest {

    @Id
    private Long id;
    private Long orderId;
    private String instId;
    private int side;
    private OrderType ordType;
    private String quantity;
    // Only for limit orders
    private String limitPrice;
    private int requestType;
    private boolean validRequest;
    private Long userId;
    private LocalDateTime createdAt = LocalDateTime.now();

}
