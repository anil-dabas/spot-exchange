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
@Entity(name = "order_fill")
@Table(name = "ex_order_fill")
public class OrderFill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tradeId;
    private Long orderId;
    private int orderType;
    private String filledQty;
    private String instId;
    private LocalDateTime matchedAt;
    private Long timestamp;
    private int orderSide;
}
