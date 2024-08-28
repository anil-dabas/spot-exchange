package com.spot.websocket.handler.ws;

import com.spot.websocket.handler.listener.event.user.UpdatedAccountEvent;
import com.spot.websocket.handler.listener.event.user.UpdatedOrderEvent;
import com.spot.websocket.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserWebSocketHandler extends BaseWebSocketHandler {

    public UserWebSocketHandler() {
        super();
    }

    @EventListener
    public void updatedAccountEventHandler(UpdatedAccountEvent event) {
        log.info("[Updated Account Event]-Delivery the data into channel {}", event.getChannel());
        String payload = JsonUtil.toJson(event.getAccountDto());
        processSessions(event.getChannel(), payload);
    }

    @EventListener
    public void updatedOrderEventHandler(UpdatedOrderEvent event) {
        log.info("[Updated Order Event]-Delivery the data into channel {}", event.getChannel());
        String payload = JsonUtil.toJson(event.getOrderDto());
        processSessions(event.getChannel(), payload);
    }

}
