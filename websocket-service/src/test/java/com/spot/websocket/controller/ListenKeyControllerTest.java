package com.spot.websocket.controller;

import com.spot.websocket.dto.user.ListenKeyDto;
import com.spot.websocket.model.response.ApiResponse;
import com.spot.websocket.service.ListenKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListenKeyControllerTest {

    @Mock
    private ListenKeyService listenKeyService;

    @InjectMocks
    private ListenKeyController listenKeyController;

    @BeforeEach
    public void setUp() {
        // Reset mock before each test
        reset(listenKeyService);
    }

    @Test
    void testCreateListenKey() {
        // Mock behavior of ListenKeyService
        String generatedKey = "generatedKey";
        when(listenKeyService.createListKey()).thenReturn(generatedKey);

        // Call the controller method
        ResponseEntity<ApiResponse<ListenKeyDto>> responseEntity = listenKeyController.createlistenKey();

        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isSuccess());
        assertEquals("Listen key has created successfully", responseEntity.getBody().getMsg());
        assertEquals(generatedKey, responseEntity.getBody().getData().getListenKey());

        // Verify service method was called
        verify(listenKeyService, times(1)).createListKey();
    }

    @Test
    void testValidateListenKey() {
        // Mock parameters and behavior
        String listenKey = "validListenKey";
        boolean isValid = true;
        when(listenKeyService.isValidListKey(listenKey)).thenReturn(isValid);

        // Call the controller method
        ResponseEntity<ApiResponse<Boolean>> responseEntity = listenKeyController.validateListenKey(listenKey);

        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isSuccess());
        assertEquals("Listen Key validation is true", responseEntity.getBody().getMsg());
        assertEquals(isValid, responseEntity.getBody().getData());

        // Verify service method was called
        verify(listenKeyService, times(1)).isValidListKey(listenKey);
    }

    @Test
    void testKeepAlive() {
        // Mock parameters and behavior
        String listenKey = "listenKeyToKeepAlive";

        // Call the controller method
        ResponseEntity<ApiResponse<Boolean>> responseEntity = listenKeyController.keepAlive(listenKey);

        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isSuccess());
        assertEquals("Listen Key was pinged succeed", responseEntity.getBody().getMsg());
        assertEquals(true, responseEntity.getBody().getData());

        // Verify service method was called
        verify(listenKeyService, times(1)).keepAliveListKey(listenKey);
    }
}
