package com.spot.websocket.handler.listener.event.market;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MarketEvent<T> extends ApplicationEvent {

    private final String channel;
    private final T payload;

    public MarketEvent(Object source, String channel, T payload) {
        super(source);
        this.channel = channel;
        this.payload = payload;
    }
}
