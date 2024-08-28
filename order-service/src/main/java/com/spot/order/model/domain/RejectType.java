package com.spot.order.model.domain;

import lombok.Getter;

@Getter
public enum RejectType {
    AMEND_REJECT(1),
    CANCEL_REJECT(2),
    PLACE_ORDER_REJECT(3);

    private final int value;

    RejectType(int value){
        this.value = value;
    }

    public static RejectType fromValue(int value) {
        for (RejectType type : RejectType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown RejectType value: " + value);
    }

}
