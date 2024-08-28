package com.spot.websocket.config;

import com.spot.websocket.service.ListenKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    private final ListenKeyService listenKeyService;

    public SchedulerConfig(ListenKeyService listenKeyService) {
        this.listenKeyService = listenKeyService;
    }

    @Scheduled(fixedRateString = "${ws.security.listen.schedule-checking-time}") // Run every minute
    public void removeExpiredListenKeys() {
        listenKeyService.removeExpiredListenKeys();
        log.info("Scheduled removal of expired listen keys executed.");
    }
}