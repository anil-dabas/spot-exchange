package com.spot.marketdata.service.kafka.listener;

import com.spot.marketdata.model.Trade;
import com.spot.marketdata.service.DBServices;
import com.spot.marketdata.service.ohlc.OHLCDataManager;
import com.spot.marketdata.service.TradeDataConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class KafkaTradeListener implements TradeDataConsumer {
    private final OHLCDataManager ohlcDataManager;
    private final DBServices dbServices;
    private final ObjectMapper objectMapper ;

    public KafkaTradeListener(OHLCDataManager ohlcDataManager, DBServices dbServices, ObjectMapper objectMapper) {
        this.ohlcDataManager = ohlcDataManager;
        this.objectMapper = objectMapper;
        this.dbServices = dbServices;
    }

    @KafkaListener(
            topics = "${kafka.listener.trade.topic}",
            groupId = "${kafka.listener.trade.groupId}"
    )
    public void consumeTradeData(String message) {
        try {
            Trade trade = objectMapper.readValue(message, Trade.class);

            log.debug("Received trade in marketDataService: {}", trade);
            ohlcDataManager.processTrade(trade);
            dbServices.saveTrade(trade);
        } catch (Exception e) {
            log.error("Error processing trade : {} for message : {}", e, message);
            e.printStackTrace(); // Handle parsing errors or log them as needed
        }
    }
}
