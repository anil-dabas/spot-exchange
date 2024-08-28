package com.spot.websocket.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@Data
public class AppConfig {

    @Value("${ws.security.jwt.secret}")
    private String secretToken;

    @Value("${ws.security.listen.expiration-time-in-minutes}")
    private long expirationTimeInMinutes;

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
