package com.spot.order.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryOrderVo {
    private String instType;
    private String instId;
    private String ordId;
    private String clOrdId;
    private String tag;
    private String orderPrice;
    private String size;
    private String accFillSz;
    private String pnl;
    private String ordType;
    private String side;
    private String posSide;
    private String tdMode;
    private String tradeId;
    private String fillTime;
    private String state;
    private String feeCcy;
    private String fee;
    private String uTime;
    private String cTime;
    private String ccy;
}
