package com.spot.order.client.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static com.spot.order.util.Constants.*;

@Slf4j
@Component
public class AssetServiceClient {

    private final RestTemplate restTemplate;

    public AssetServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getBalanceForSymbol(String currencySymbol, Long userId) {
        String url = String.format(BALANCE_URL_TEMPLATE, currencySymbol, userId);
        return makeRequest(url, HttpMethod.GET, BigDecimal.class, null);
    }

    public Boolean freezeBalance(String currencySymbol, BigDecimal amount, Long userId) {
        String url = String.format(FREEZE_URL_TEMPLATE, currencySymbol, amount, userId);
        return makeRequest(url, HttpMethod.POST, Boolean.class, null);
    }

    public Boolean unFreezeBalance(String currencySymbol, BigDecimal amount, Long userId) {
        String url = String.format(UNFREEZE_URL_TEMPLATE, currencySymbol, amount, userId);
        return makeRequest(url, HttpMethod.POST, Boolean.class, null);
    }

    public Boolean transferBalance(Long fromUserId,Long toUserId, String currencySymbol, BigDecimal amount) {
        String url = String.format(TRANSFER_URL_TEMPLATE, fromUserId,toUserId, currencySymbol, amount);
        return makeRequest(url, HttpMethod.POST, Boolean.class, null);
    }

    private <T> T makeRequest(String url, HttpMethod method, Class<T> responseType, Object requestBody) {
        log.info("The URL that we are trying to hit is URL {} with token {}",url,ASSET_SERVICE_TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ASSET_SERVICE_TOKEN);
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
        return response.getBody();
    }
}
