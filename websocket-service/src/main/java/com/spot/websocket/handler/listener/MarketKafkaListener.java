package com.spot.websocket.handler.listener;

import com.spot.websocket.dto.market.DepthDto;
import com.spot.websocket.dto.market.TickerDto;
import com.spot.websocket.dto.market.TradeDto;
import com.spot.websocket.handler.listener.event.market.MarketEvent;
import com.spot.websocket.mapper.MarketMapper;
import com.spot.websocket.model.market.Depth;
import com.spot.websocket.model.market.Ticker;
import com.spot.websocket.model.market.Trade;
import com.spot.websocket.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MarketKafkaListener {

    private static final String TICKER_CHANNEL = "ticker";
    private static final String DEPTH_CHANNEL = "depth";
    private static final String TRADE_CHANNEL = "trade";

    private final ApplicationEventPublisher applicationEventPublisher;

    public MarketKafkaListener(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @KafkaListener(topics = "${ws.topic.market.depth}", groupId = "${ws.group-id.market}")
    void onDepthMessage(String payload) {
        log.info("[Depth]-Receives the message {}", payload);
        Depth depth = JsonUtil.fromJson(payload, Depth.class);
        DepthDto dto = MarketMapper.INSTANCE.depthDto(depth);
        // publish event
        String channel = String.format("%s@%s", DEPTH_CHANNEL, depth.getSymbol());
        MarketEvent<DepthDto> event = new MarketEvent<>(this, channel, dto);

        applicationEventPublisher.publishEvent(event);
    }

    @KafkaListener(topics = "${ws.topic.market.ticker}", groupId = "${ws.group-id.market}")
    void onTickerMessage(String payload) {
        log.info("[Ticker]-Receives the message {}", payload);
        Ticker ticker = JsonUtil.fromJson(payload, Ticker.class);
        TickerDto dto = MarketMapper.INSTANCE.tickerDto(ticker);
        // publish event
        String channel = String.format("%s@%s", TICKER_CHANNEL, ticker.getSymbol());
        MarketEvent<TickerDto> event = new MarketEvent<>(this, channel, dto);
        applicationEventPublisher.publishEvent(event);
    }

    @KafkaListener(topics = "${ws.topic.market.trade}", groupId = "${ws.group-id.market}")
    void onTradeMessage(String payload) {
        log.info("[Trades]-Receives the message {}", payload);
        Trade trade = JsonUtil.fromJson(payload, Trade.class);
        TradeDto dto = MarketMapper.INSTANCE.tradeDto(trade);
        // publish event
        String channel = String.format("%s@%s", TRADE_CHANNEL, trade.getSymbol());
        MarketEvent<TradeDto> event = new MarketEvent<TradeDto>(this, channel, dto);
        applicationEventPublisher.publishEvent(event);
    }

}
