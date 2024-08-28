package com.spot.websocket.service;

import com.spot.websocket.config.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListenKeyServiceTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private Clock clock;

    @InjectMocks
    private ListenKeyService listenKeyService;

    @BeforeEach
    public void setUp() {
        when(appConfig.getExpirationTimeInMinutes()).thenReturn(5L); // 5 minutes expiration time
        listenKeyService = new ListenKeyService(appConfig, clock);
    }

    @Test
    void testCreateListKey() {
        when(clock.millis()).thenReturn(Instant.now().toEpochMilli());
        String listenKey = listenKeyService.createListKey();
        assertNotNull(listenKey);
        assertTrue(listenKeyService.isValidListKey(listenKey));
    }

    @Test
    void testIsValidListKey() {
        long currentTimeMillis = Instant.now().toEpochMilli();
        when(clock.millis()).thenReturn(currentTimeMillis);

        String listenKey = listenKeyService.createListKey();
        assertTrue(listenKeyService.isValidListKey(listenKey));

        // Mock current time to be beyond expiration time
        when(clock.millis()).thenReturn(currentTimeMillis + (6 * 60 * 1000)); // 6 minutes later

        assertFalse(listenKeyService.isValidListKey(listenKey));
    }

    @Test
    void testKeepAliveListKey() {
        when(clock.millis()).thenReturn(Instant.now().toEpochMilli());
        String listenKey = listenKeyService.createListKey();
        listenKeyService.keepAliveListKey(listenKey);
        assertTrue(listenKeyService.isValidListKey(listenKey));
    }

    @Test
    void testRemoveExpiredListenKeys() {
        long currentTimeMillis = Instant.now().toEpochMilli();
        when(clock.millis()).thenReturn(currentTimeMillis);

        String listenKey1 = listenKeyService.createListKey();

        // Mock current time to be beyond expiration time for listenKey1
        when(clock.millis()).thenReturn(currentTimeMillis + (6 * 60 * 1000)); // 6 minutes later
        String listenKey2 = listenKeyService.createListKey();

        listenKeyService.removeExpiredListenKeys();

        assertFalse(listenKeyService.isValidListKey(listenKey1));
        assertTrue(listenKeyService.isValidListKey(listenKey2));
    }
}
