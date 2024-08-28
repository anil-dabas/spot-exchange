//package com.spot.websocket.handler.ws;
//
//
//import com.spot.websocket.dto.stream.StreamReqDto;
//import com.spot.websocket.util.JsonUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//class BaseWebSocketHandlerTest {
//
//    @Mock
//    private WebSocketSession webSocketSession;
//
//    @InjectMocks
//    private BaseWebSocketHandler webSocketHandler = new BaseWebSocketHandler() {
//        @Override
//        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//            // Override abstract method for testing purposes
//            session.sendMessage(message);
//        }
//    };
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testAfterConnectionEstablished() throws Exception {
//        webSocketHandler.afterConnectionEstablished(webSocketSession);
//        // Add assertions or verify behavior as needed
//    }
//
//    @Test
//    void testAfterConnectionClosed() throws Exception {
//        CloseStatus closeStatus = new CloseStatus(1000, "Normal closure");
//        webSocketHandler.afterConnectionClosed(webSocketSession, closeStatus);
//        // Add assertions or verify behavior as needed
//    }
//
//    @Test
//    void testHandleMessage() throws Exception {
//        StreamReqDto streamReqDto = new StreamReqDto();
//        streamReqDto.setMethod("SUBSCRIBE");
//        List<String> params = new ArrayList<>();
//        params.add("channel1");
//        streamReqDto.setParams(params);
//        //prepare
//        String payload = JsonUtil.toJson(streamReqDto);
//        TextMessage textMessage = new TextMessage(payload);
//        webSocketHandler.handleMessage(webSocketSession, textMessage);
//
//        // Verify that appropriate methods are called or behaviors are as expected
//        verify(webSocketSession, times(1)).sendMessage(any(TextMessage.class));
//    }
//
//    @Test
//    void testProcessSessions() throws IOException, NoSuchFieldException, IllegalAccessException {
//        String channel = "channel1";
//        String payload = "testPayload";
//
//        Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
//        sessions.add(webSocketSession);
//
//        ConcurrentHashMap<String, Set<WebSocketSession>> subscribers = new ConcurrentHashMap<>();
//        subscribers.put(channel, sessions);
//
//        // Set subscribers map using reflection (since it's private in BaseWebSocketHandler)
//        java.lang.reflect.Field field = BaseWebSocketHandler.class.getDeclaredField("subscribers");
//        field.setAccessible(true);
//        field.set(webSocketHandler, subscribers);
//
//        webSocketHandler.processSessions(channel, payload);
//
//        // Verify that sendMessageToSession is called for each session
//        verify(webSocketSession, times(1)).sendMessage(any(TextMessage.class));
//    }
//}
