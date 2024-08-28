package com.spot.marketdata.service.kafka.listener;

import com.spot.marketdata.model.MarketDepth;
import com.spot.marketdata.service.depth.DepthDataHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaDepthListener {



    private final ObjectMapper objectMapper;

    private final DepthDataHandler depthDataHandler;

    public KafkaDepthListener(DepthDataHandler depthDataHandler){
        this.depthDataHandler = depthDataHandler;
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MarketDepth.class, new MarketDepthDeserializer());
        objectMapper.registerModule(module);
    }

    @KafkaListener(
            topics = "${kafka.listener.depth.topic}",
            groupId = "${kafka.listener.depth.groupId}"
    )
    public void mktDepthListener(String message) {
        log.debug("MarketDepthListener received message: {}", message);
        try {
            MarketDepth marketDepth = objectMapper.readValue(message, MarketDepth.class);
            depthDataHandler.handleNewDepth(marketDepth);

        } catch (Exception e) {
            log.error("Error processing market depth message: ", e);
        }
    }
}
