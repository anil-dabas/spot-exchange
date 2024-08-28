package com.spot.order.model.domain;

import lombok.Getter;

@Getter
public enum OrderType {
    LIMIT(1),
    MARKET(2),
    STOP_LOSS(3);

    private final int value;

    OrderType(int value) {
        this.value = value;
    }

    public static OrderType fromValue(int value) {
        for (OrderType type : OrderType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown OrderType value: " + value);
    }

}
