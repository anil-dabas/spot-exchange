package com.spot.order.kafka;


import com.spot.order.model.domain.*;
import com.spot.order.model.payload.listener.AmendedOrderUpdate;
import com.spot.order.model.payload.listener.CanceledOrderUpdate;
import com.spot.order.model.payload.listener.ExpiredOrderUpdate;
import com.spot.order.model.payload.listener.RejectedOrderUpdate;
import com.spot.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spot.order.model.domain.TradeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaOrderListener {

    @Autowired
    OrderService orderService;

    private final ObjectMapper objectMapper;

    public KafkaOrderListener(){
        this.objectMapper = new ObjectMapper();
    }
    @KafkaListener(topics = "${kafka.topic.orderstatus.matched}", groupId = "order_group")
    public void listenMatchedOrdersUpdate(String message) {
        try {
            TradeResponse tradeResponse = objectMapper.readValue(message, TradeResponse.class);
            orderService.updateMatchedOrderDetailsAndPublishResponse(tradeResponse);
            log.info("Received trade response: {}", tradeResponse);
        } catch (JsonProcessingException e) {
            log.info("Error processing message: {}" , e.getMessage());
        }

    }

    @KafkaListener(topics = "${kafka.topic.orderstatus.canceled}", groupId = "order_group")
    public void listenCanceledOrderUpdate(String message) {
        try {
            CanceledOrderUpdate orderStatus = objectMapper.readValue(message, CanceledOrderUpdate.class);
            orderService.updateCancelledOrderToQueue(orderStatus);
            log.info("Received order status canceled: {}", orderStatus);
        } catch (JsonProcessingException e) {
            log.info("Error processing message: {}" , e.getMessage());
        }

    }


    @KafkaListener(topics = "${kafka.topic.orderstatus.expired}", groupId = "order_group")
    public void listenExpiredOrderUpdate(String message) {
        try {
            ExpiredOrderUpdate orderStatus = objectMapper.readValue(message, ExpiredOrderUpdate.class);
            orderService.updateExpiredOrderToQueue(orderStatus);
            log.info("Received order status expired: {}", orderStatus);
        } catch (JsonProcessingException e) {
            log.info("Error processing message expired: {}" , e.getMessage());
        }

    }


    @KafkaListener(topics = "${kafka.topic.orderstatus.rejected}", groupId = "order_group")
    public void listenRejectedOrderUpdate(String message) {
        try {
            RejectedOrderUpdate orderStatus = objectMapper.readValue(message, RejectedOrderUpdate.class);
            orderService.updateRejectedOrderToQueue(orderStatus);
            log.info("Received order status rejected: {}", orderStatus);
        } catch (JsonProcessingException e) {
            log.info("Error processing message rejected: {}" , e.getMessage());
        }

    }

    @KafkaListener(topics = "${kafka.topic.orderstatus.amended}", groupId = "order_group")
    public void listenAmendedOrderUpdate(String message) {
        try {
            AmendedOrderUpdate orderStatus = objectMapper.readValue(message, AmendedOrderUpdate.class);
            orderService.updateAmendedOrderToQueue(orderStatus);
            log.info("Received order status amended: {}", orderStatus);
        } catch (JsonProcessingException e) {
            log.info("Error processing message amended: {}" , e.getMessage());
        }

    }
}

