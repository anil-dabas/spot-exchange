package com.spot.marketdata.service.ohlc;

import com.spot.marketdata.model.OHLC;

public interface OhlcPublisher {
    void publishOhlc(OHLC ohlc);
}
