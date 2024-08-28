package com.spot.websocket.service;

import com.spot.websocket.config.AppConfig;
import com.spot.websocket.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ListenKeyService {

    private final Map<String, Long> listenKeys;
    private final AppConfig appConfig;
    private final long expirationTimeInMillis;
    private final Clock clock;

    public ListenKeyService(AppConfig appConfig, Clock clock) {
        this.appConfig = appConfig;
        this.listenKeys = new ConcurrentHashMap<>();
        this.clock = clock;
        // covert config to millis
        expirationTimeInMillis = appConfig.getExpirationTimeInMinutes() * 60 * 1000;
    }

    private long getCurrentTimeMillis() {
        return clock.millis();
    }

    public String createListKey() {
        String listenKey = Util.generateListKey();
        long currentTimeMillis = getCurrentTimeMillis();
        listenKeys.put(listenKey, currentTimeMillis);
        return listenKey;
    }

    public boolean isValidListKey(String listKey) {
        Long creationTime = listenKeys.get(listKey);
        if (creationTime == null) {
            return false;
        }
        long currentTimeMillis = getCurrentTimeMillis();
        return (currentTimeMillis - creationTime) < expirationTimeInMillis;
    }

    public void keepAliveListKey(String listKey) {
        if (isValidListKey(listKey)) {
            listenKeys.put(listKey, getCurrentTimeMillis());
        }
    }

    public void removeExpiredListenKeys() {
        long currentTimeMillis = getCurrentTimeMillis();
        log.info("Scheduled removal of expired listen keys at time {}", currentTimeMillis);
        listenKeys.entrySet().removeIf(entry -> (currentTimeMillis - entry.getValue()) >= expirationTimeInMillis);
    }
}
