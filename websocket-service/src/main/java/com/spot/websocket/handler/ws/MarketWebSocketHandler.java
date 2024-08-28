package com.spot.websocket.handler.ws;

import com.spot.websocket.handler.listener.event.market.MarketEvent;
import com.spot.websocket.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MarketWebSocketHandler extends BaseWebSocketHandler {

    @EventListener
    public void onMarketEventHandler(MarketEvent<?> event) {
        log.info("[Market]-Delivery the data into websocket");
        String payload = JsonUtil.toJson(event.getPayload());
        processSessions(event.getChannel(), payload);
    }
}
