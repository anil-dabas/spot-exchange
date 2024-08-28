package com.spot.order.util;

import java.math.BigDecimal;

public class Constants {
    public static final int SUCCESS = 0;
    public static final BigDecimal MARKET_MARGIN_BUFFER = new BigDecimal("1.03");
    public static final int  FAILURE = 1;
    public static final long DUMMY_ORDER_ID = -1;
    public static final String ASSET_SERVICE_BASE_INNER_URL = "http://localhost:8088/inner/v1/account";
    public static final String BALANCE_URL_TEMPLATE = ASSET_SERVICE_BASE_INNER_URL + "/balance?ccy=%s&userId=%d";
    public static final String FREEZE_URL_TEMPLATE = ASSET_SERVICE_BASE_INNER_URL + "/freeze?ccy=%s&amount=%s&userId=%d";
    public static final String UNFREEZE_URL_TEMPLATE = ASSET_SERVICE_BASE_INNER_URL + "/unfreeze?ccy=%s&amount=%s&userId=%d";
    public static final String TRANSFER_URL_TEMPLATE = ASSET_SERVICE_BASE_INNER_URL + "/transfer?fromUserId=%d&toUserId=%d&ccy=%s&amount=%s";
    public static final String ASSET_SERVICE_TOKEN ="Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzeXN0ZW0iLCJpYXQiOjE3MjM2MjUxNDV9.ZbZYzxUu9kYxi9A0ke0oqCPczluCYWT2bPoKLADWh6U";
}
