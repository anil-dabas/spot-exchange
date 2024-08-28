package com.spot.order.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "freezeBalance")
@Table(name = "freezeBalance")
public class FreezeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long freezeId;
    private String currencySymbol;
    private BigDecimal frozenBalance;
    private Long userId;
    private Long requestId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private boolean validFreeze = true;
}
