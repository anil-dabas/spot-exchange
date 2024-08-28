package com.spot.websocket.handler.listener.event.user;

import com.spot.websocket.dto.user.AccountDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdatedAccountEvent extends ApplicationEvent {

    private final String channel;
    private final AccountDto accountDto;

    public UpdatedAccountEvent(Object source, String channel, AccountDto dto) {
        super(source);
        this.accountDto = dto;
        this.channel = channel;
    }
}
