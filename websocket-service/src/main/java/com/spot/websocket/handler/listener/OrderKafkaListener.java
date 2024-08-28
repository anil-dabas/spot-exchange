package com.spot.websocket.handler.listener;

import com.spot.websocket.dto.user.OrderDto;
import com.spot.websocket.handler.listener.event.user.UpdatedOrderEvent;
import com.spot.websocket.mapper.UserMapper;
import com.spot.websocket.model.user.Order;
import com.spot.websocket.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class OrderKafkaListener {

    public static final String ORDER_CHANNEL = "order";
    private final ApplicationEventPublisher applicationEventPublisher;

    @KafkaListener(topics = "${ws.topic.trade.order}", groupId = "${ws.group-id.trade}")
    void onMessage(String payload) {
        log.info("[Order]-Receives the message {}", payload);
        Order order = JsonUtil.fromJson(payload, Order.class);
        OrderDto dto = UserMapper.INSTANCE.toOrderDto(order);

        // publish event
        String channel = String.format("%s@%s", order.getUserId(), ORDER_CHANNEL);
        UpdatedOrderEvent event = new UpdatedOrderEvent(this, channel, dto);
        applicationEventPublisher.publishEvent(event);
    }



}
