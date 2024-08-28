package com.spot.websocket.handler.listener;

import com.spot.websocket.dto.user.AccountDto;
import com.spot.websocket.handler.listener.event.user.UpdatedAccountEvent;
import com.spot.websocket.mapper.UserMapper;
import com.spot.websocket.model.user.Account;
import com.spot.websocket.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class AccountKafkaListener {

    public static final String ACCOUNT_CHANNEL = "account";
    private final ApplicationEventPublisher applicationEventPublisher;

    @KafkaListener(topics = "${ws.topic.trade.account}", groupId = "${ws.group-id.trade}")
    void onMessage(String payload) {
        log.info("[Account]-Receives the message {}", payload);
        Account account = JsonUtil.fromJson(payload, Account.class);
        AccountDto dto = UserMapper.INSTANCE.toAccountDto(account);

        // publish event
        String channel = String.format("%s@%s", account.getUserId(), ACCOUNT_CHANNEL);
        UpdatedAccountEvent event = new UpdatedAccountEvent(this, channel, dto);
        applicationEventPublisher.publishEvent(event);
    }
}
