package com.spot.websocket.config;

import com.spot.websocket.handler.ws.MarketWebSocketHandler;
import com.spot.websocket.handler.ws.UserWebSocketHandler;
import com.spot.websocket.service.ListenKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final MarketWebSocketHandler marketHandler;

    private final UserWebSocketHandler userWebSocketHandler;

    private final ListenKeyService listenKeyService;

    private final String MARKET_PATH = "/stream";
    private final String USER_PATH = "/user-stream";

    public WebSocketConfig(MarketWebSocketHandler marketHandler, UserWebSocketHandler userWebSocketHandler, ListenKeyService listenKeyService) {
        this.marketHandler = marketHandler;
        this.userWebSocketHandler = userWebSocketHandler;
        this.listenKeyService = listenKeyService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // public ws
        registry.addHandler(marketHandler, MARKET_PATH)
                .setAllowedOrigins("*");

        // private
        registry.addHandler(userWebSocketHandler, USER_PATH)
                .setAllowedOrigins("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        String listenKey = request.getHeaders().getFirst("listen-key");
                        log.info("Checking apiKey is existed or not {}", listenKey);
                        if (listenKey == null || !listenKeyService.isValidListKey(listenKey)) {
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            log.warn("No apiKey is existed in header. Pls add its before go!!");
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {

                    }
                });
    }
}