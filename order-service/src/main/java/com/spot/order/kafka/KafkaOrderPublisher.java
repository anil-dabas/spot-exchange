package com.spot.order.kafka;

import com.spot.order.model.payload.publisher.OrderRequestPayload;
import com.spot.order.model.payload.publisher.UpdateOrderPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaOrderPublisher {
    @Value("${kafka.topic.order}")
    private String orderTopic;

    @Value("${kafka.topic.orderUpdate}")
    private String orderUpdateTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOrderPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

//    public boolean publishOrderUpdate(UpdateOrderPayload updateOrderPayload) {
//        try {
//            String message = objectMapper.writeValueAsString(updateOrderPayload);
//            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(orderUpdateTopic, message);
//
//            try {
//                SendResult<String, String> result = future.get(); // Wait for the result
//                log.debug("Successfully published OrderUpdate to Kafka: {}", message);
//                return true;
//            } catch (ExecutionException | InterruptedException e) {
//                log.error("Failed to publish OrderUpdate to Kafka: {}", message, e);
//                return false;
//            }
//        } catch (JsonProcessingException e) {
//            log.error("Error while serializing OrderUpdate data: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    public boolean publishOrder(OrderRequestPayload order, String instId) {
//        try {
//            orderTopic = orderTopic.replace("{symbol}", instId);
//            String message = objectMapper.writeValueAsString(order);
//            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(orderTopic, message);
//
//            try {
//                SendResult<String, String> result = future.get(); // Wait for the result
//                log.debug("Successfully published Order to Kafka: {}", message);
//                return true;
//            } catch (ExecutionException | InterruptedException e) {
//                log.error("Failed to publish Order to Kafka: {}", message, e);
//                return false;
//            }
//        } catch (JsonProcessingException e) {
//            log.error("Error while serializing Order data: {}", e.getMessage());
//            return false;
//        }
//    }




    public void publishOrderUpdate(UpdateOrderPayload updateOrderPayload){
        try {
            String message = objectMapper.writeValueAsString(updateOrderPayload);
            kafkaTemplate.send(orderUpdateTopic, message);
            log.info("Publishing OrderUpdate to Kafka: {}", message);
        } catch (JsonProcessingException e) {
            log.info("Error while serializing OrderUpdate data: {}", e.getMessage());
        }
    }

    public void publishOrder(OrderRequestPayload order, String instId) {
        try {
            String currentTopic  = orderTopic.replace("{symbol}",instId);
            String message = objectMapper.writeValueAsString(order);
            kafkaTemplate.send(currentTopic, message);
            log.debug("Publishing Order to Kafka: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Error while serializing Order data: {}", e.getMessage());
        }
    }
}
