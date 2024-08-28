package com.spot.marketdata.service.kafka.publisher;

import com.spot.marketdata.model.OHLC;
import com.spot.marketdata.service.ohlc.OhlcPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//@Service
@Slf4j
public class KafkaOhlcPublisher implements OhlcPublisher {
    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOhlcPublisher(@Value("${kafka.topic.ohlc}") String topic,
                              KafkaTemplate<String, String> kafkaTemplate,
                              ObjectMapper objectMapper) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishOhlc(OHLC ohlc) {
        try {
            String message = objectMapper.writeValueAsString(ohlc);
            kafkaTemplate.send(topic, message);
            log.debug("Publishing OHLC to Kafka: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Error while serializing OHLC data: {}", e.getMessage());
        }
    }
}
