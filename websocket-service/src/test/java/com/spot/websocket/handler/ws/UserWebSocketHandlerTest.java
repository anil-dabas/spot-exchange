//package com.spot.websocket.handler.ws;
//
//import com.spot.websocket.dto.user.AccountDto;
//import com.spot.websocket.dto.user.OrderDto;
//import com.spot.websocket.model.event.user.UpdatedAccountEvent;
//import com.spot.websocket.model.event.user.UpdatedOrderEvent;
//import com.spot.websocket.util.JsonUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//
//import static org.mockito.Mockito.eq;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class UserWebSocketHandlerTest {
//
//    @Mock
//    private JsonUtil jsonUtil;
//
//    @Mock
//    private SimpMessagingTemplate messagingTemplate;
//
//    @InjectMocks
//    private UserWebSocketHandler webSocketHandler;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    void testUpdatedAccountEventHandler() {
////        AccountDto accountDto = new AccountDto();
//        UpdatedAccountEvent event = new UpdatedAccountEvent(this, "channel-1", new AccountDto());
//        String jsonPayload = "{\"account\": \"account-1\"}";
//
//        // Mock behavior of JsonUtil.toJson()
//        when(JsonUtil.toJson(event.getAccountDto())).thenReturn(jsonPayload);
//
//        // Call the event handler method
//        webSocketHandler.updatedAccountEventHandler(event);
//
//        // Verify that processSessions() was called with the correct arguments
//        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/channel-1"), eq(jsonPayload));
//    }
//
////    @Test
////    void testUpdatedOrderEventHandler() {
////        UpdatedOrderEvent event = new UpdatedOrderEvent("channel-2", new OrderDto("order-1"));
////        String jsonPayload = "{\"order\": \"order-1\"}";
////
////        // Mock behavior of JsonUtil.toJson()
////        when(JsonUtil.toJson(event.getOrderDto())).thenReturn(jsonPayload);
////
////        // Call the event handler method
////        webSocketHandler.updatedOrderEventHandler(event);
////
////        // Verify that processSessions() was called with the correct arguments
////        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/channel-2"), eq(jsonPayload));
////    }
//}
