package com.spot.marketdata.service.redis;

import com.spot.marketdata.model.OHLC;
import com.spot.marketdata.service.ohlc.OhlcPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class RedisOhlcPublisher implements OhlcPublisher {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper ;

    public RedisOhlcPublisher(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper){
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishOhlc(OHLC ohlc) {
        try {
            String ohlcDataJson = objectMapper.writeValueAsString(ohlc);
            stringRedisTemplate.opsForValue().set("ohlcData:" + ohlc.getSymbol() + ":" + ohlc.getInterval(), ohlcDataJson);
            log.debug("Publishing OHLC Data to Redis for {}: {}", ohlc.getSymbol(), ohlcDataJson);
        } catch (JsonProcessingException e) {
            log.error("Error serializing OHLC data", e);
        }
    }
}
