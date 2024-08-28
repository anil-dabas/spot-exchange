package com.spot.websocket.handler.ws;

import com.spot.websocket.dto.stream.StreamReqDto;
import com.spot.websocket.dto.stream.StreamRespDto;
import com.spot.websocket.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public abstract class BaseWebSocketHandler implements WebSocketHandler {

    // channels - users
    private final Map<String, Set<WebSocketSession>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established on session:{}", session.getId());
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Connection closed on session:{} with status: {}", session.getId(), closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void processStreamReq(StreamReqDto streamReqDto, WebSocketSession session) throws IOException {
        log.info("Starting to process stream request: {}", session.getId());

        if ("SUBSCRIBE".equals(streamReqDto.getMethod())) {
            subscribe(streamReqDto.getParams(), session);
        } else if ("UNSUBSCRIBE".equals(streamReqDto.getMethod())) {
            unsubscribe(streamReqDto.getParams(), session);
        } else {
            session.sendMessage(new TextMessage("Invalid request"));
        }
    }

    protected void subscribe(List<String> channels, WebSocketSession session) {
        log.info("Subscribes the channels {}", channels);
        for (String channel : channels) {
            subscribers.computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>()).add(session);
        }
    }

    protected void unsubscribe(List<String> channels, WebSocketSession session) {
        log.info("Unsubscribe the channels {} for session : {}", channels, session.getId());
        for (String channel : channels) {
            Set<WebSocketSession> sessions = subscribers.get(channel);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    subscribers.remove(channel);
                }
            }
        }
    }

    // Method to process sessions based on key
    public void processSessions(String channel, String payload) {
        Set<WebSocketSession> sessions = subscribers.get(channel);
        log.info("Processing sessions for topic: {} ", channel);
        if(sessions == null) return;
        // Process each WebSocket session in the list
        for (WebSocketSession session : sessions) {
            if(session.isOpen()) {
                sendMessageToSession(session, payload);
            } else {
                sessions.remove(session);
            }
        }
    }

    // Method to send a message to a WebSocket session
    private void sendMessageToSession(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
        log.info("Payload request : {}", payload);
        // process the stream request
        StreamReqDto streamReqDto = JsonUtil.fromJson(payload, StreamReqDto.class);
        processStreamReq(streamReqDto, session);

        StreamRespDto response = StreamRespDto.builder()
                .id(UUID.randomUUID())
                .result("OK")
                .build();
        session.sendMessage(new TextMessage(Objects.requireNonNull(JsonUtil.toJson(response))));
    }
}
