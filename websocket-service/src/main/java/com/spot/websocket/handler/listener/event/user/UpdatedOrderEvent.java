package com.spot.websocket.handler.listener.event.user;

import com.spot.websocket.dto.user.OrderDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdatedOrderEvent extends ApplicationEvent {

    private final String channel;

    private final OrderDto orderDto;

    public UpdatedOrderEvent(Object object,String channel, OrderDto dto) {
        super(object);
        this.channel = channel;
        this.orderDto = dto;
    }
}
