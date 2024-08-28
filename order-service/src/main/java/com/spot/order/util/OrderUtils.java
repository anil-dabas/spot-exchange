package com.spot.order.util;

import com.spot.order.model.domain.*;
import com.spot.order.model.domain.Order;
import com.spot.order.model.domain.OrderFinish;
import com.spot.order.model.domain.OrderRequest;
import com.spot.order.model.domain.OrderType;
import com.spot.order.model.payload.publisher.UpdateOrderPayload;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.spot.order.model.domain.OrderSide.fromValue;
import static com.spot.order.util.TradeUtil.convertToMicroseconds;

public class OrderUtils {

    public static UpdateOrderPayload createUpdateOrderPayloadFromOrder(Order order) {
        return UpdateOrderPayload.builder()
                .id(order.getOrderId())
                .userId(order.getUserId())
                .idStr(String.valueOf(order.getOrderId()))
                .createdAt(order.getTimestamp())
                .updatedAt(order.getUpdatedAt().toEpochSecond(ZoneOffset.UTC) * 1000)
                .symbol(order.getInstId())
                .type(OrderType.fromValue(order.getOrderType()))
                .side(fromValue(order.getSide()))
                .price(order.getLimitPrice() != null ? order.getLimitPrice().toString() : "")
                .quantity(order.getQuantity())
                .quoteQuantity(order.getQuoteQuantity())
                .status(order.getState().name())
                .timeInForce(order.getTimeInForce())
                .executedQuantity(order.getExecutedQuantity())
                .executedQuoteQuantity(order.getExecutedQuoteQuantity())
                .build();
    }

    public static UpdateOrderPayload createUpdateOrderPayloadFromOrderRequest(OrderRequest orderRequest) {
        // TODO make it more informative
        return UpdateOrderPayload.builder()
                .id(orderRequest.getOrderId())
                .userId(orderRequest.getUserId())
                .side(fromValue(orderRequest.getSide()))
                .createdAt(convertToMicroseconds(orderRequest.getCreatedAt()))
                .updatedAt(convertToMicroseconds(LocalDateTime.now()))
                .symbol(orderRequest.getInstId())
                //.type(orderRequest.getRequestType())
                .build();
    }

    public static OrderFinish createOrderFinishFromOrder(Order order) {
        return OrderFinish.builder()
                .userId(order.getUserId())
                .timestamp(order.getTimestamp())
                .requestId(order.getRequestId())
                .orderId(order.getOrderId())
                .side(order.getSide())
                .orderType(order.getOrderType())
                .quantity(order.getQuantity())
                .limitPrice(order.getLimitPrice())
                .quoteQuantity(order.getQuoteQuantity())
                .instId(order.getInstId())
                .createdAt(order.getCreatedAt())
                .state(order.getState())
                .updatedAt(order.getUpdatedAt())
                .fillPrice(order.getFillPrice())
                .timeInForce(order.getTimeInForce())
                .build();
    }

}
