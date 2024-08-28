package com.spot.websocket.mapper;

import com.spot.websocket.dto.market.DepthDto;
import com.spot.websocket.dto.market.TickerDto;
import com.spot.websocket.dto.market.TradeDto;
import com.spot.websocket.model.market.Depth;
import com.spot.websocket.model.market.Ticker;
import com.spot.websocket.model.market.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MarketMapper {

    MarketMapper INSTANCE = Mappers.getMapper(MarketMapper.class);

    @Mappings({
            @Mapping(expression = "java(java.time.Instant.now().toEpochMilli())", target = "data.eventTime"),
            @Mapping(source = "symbol", target = "data.symbol"),
            @Mapping(source = "price", target = "data.price"),
            @Mapping(source = "quantity", target = "data.quantity"),
            @Mapping(source = "buyerMaker", target = "data.buyerMarketMaker"),
            @Mapping(source = "timestamp", target = "data.tradeTime"),
            @Mapping(source = "sell", target = "data.sellerOrderId"),
            @Mapping(source = "buy", target = "data.buyerOrderId"),
            @Mapping(constant = "trade", target = "data.evenType"),
            @Mapping(expression = "java(trade.getBuy())", target = "data.tradeId"),
            @Mapping(expression = "java(\"trade@\" + trade.getSymbol())", target = "stream")
    })
    TradeDto tradeDto(Trade trade);

    @Mappings({
            @Mapping(expression = "java(java.time.Instant.now().toEpochMilli())", target = "data.eventTime"),
            @Mapping(source = "symbol", target = "data.symbol"),
            @Mapping(source = "asks", target = "data.asks"),
            @Mapping(source = "bids", target = "data.bids"),
            @Mapping(source = "timestamp", target = "data.tradeTime"),
            @Mapping(constant = "depth", target = "data.evenType"),
            @Mapping(expression = "java(\"depth@\" + depth.getSymbol())", target = "stream")
    })
    DepthDto depthDto(Depth depth);


    @Mappings({
            @Mapping(expression = "java(java.time.Instant.now().toEpochMilli())", target = "data.eventTime"),
            @Mapping(source = "symbol", target = "data.symbol"),
            @Mapping(source = "quoteVolume", target = "data.quoteVolume"),
            @Mapping(source = "lastPrice", target = "data.lastPrice"),
            @Mapping(constant = "ticker", target = "data.eventType"),
            @Mapping(source = "high", target = "data.high"),
            @Mapping(source = "low", target = "data.low"),
            @Mapping(source = "trades", target = "data.trades"),
            @Mapping(source = "firstPrice", target = "data.firstPrice"),
            @Mapping(source = "volume", target = "data.volume"),
            @Mapping(expression = "java(\"ticker@\" + ticker.getSymbol())", target = "stream")
    })
    TickerDto tickerDto(Ticker ticker);

}
