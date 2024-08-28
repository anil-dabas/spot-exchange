package com.spot.marketdata.service.kafka.publisher;

import com.spot.marketdata.model.Ticker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaTickerPublisher {
    @Value("${kafka.topic.ticker}")
    private String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaTickerPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishTicker(Ticker ticker) {
        try {
            String message = objectMapper.writeValueAsString(ticker);
            kafkaTemplate.send(topic, message);
            log.debug("Publishing Ticker to Kafka: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Error while serializing Ticker data: {}", e.getMessage());
        }
    }
}
