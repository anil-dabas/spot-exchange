package com.spot.marketdata.config;

import com.spot.marketdata.service.kafka.publisher.KafkaOhlcPublisher;
import com.spot.marketdata.service.ohlc.OhlcPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class OhlcPublisherConfig {

    @Bean
    @Primary // This annotation makes this bean the default choice unless overridden
    public OhlcPublisher kafkaOhlcPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                            ObjectMapper objectMapper,
                                            @Value("${kafka.topic.ohlc}") String ohlcTopic) {
        return new KafkaOhlcPublisher(ohlcTopic, kafkaTemplate, objectMapper);
    }

  /*  @Bean
    public OhlcPublisher rabbitMqOhlcPublisher() {
        return new RabbitMqOhlcPublisher(); // Assuming you have this class implemented
    }*/
}
