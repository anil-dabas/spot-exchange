# Market Websocket API

## Overview

The Market Websocket API allows you to subscribe to various market data streams such as trade, depth, ticker, and kline for different trading pairs. This documentation provides the details on how to subscribe and unsubscribe to these data streams.

## WebSocket Channels

### Trade
- **Channel Name:** `trade@symbol`
- **Description:** Provides real-time trade data for the specified symbol.

### Depth
- **Channel Name:** `depth@symbol`
- **Description:** Provides real-time order book depth data for the specified symbol.

### Ticker
- **Channel Name:** `ticker@symbol`
- **Description:** Provides real-time ticker data for the specified symbol.

### Kline
- **Channel Name:** `kline@symbol`
- **Description:** Provides real-time kline/candlestick data for the specified symbol.

## Subscribe to WebSocket Channels

To subscribe to one or more WebSocket channels, send a message with the `SUBSCRIBE` method and the desired channels in the `params` array.

### Example Request

#### SUBSCRIBE JSON Payload
```json
{
    "method": "SUBSCRIBE",
    "params": [
        "trade@BTC_USDT",
        "depth@BTC_USDT",
        "ticker@BTC_USDT"
    ]
}
```

#### UNSUBSCRIBE JSON Payload
```json
{
    "method": "UNSUBSCRIBE",
    "params": [
        "trade@BTC_USDT",
        "depth@BTC_USDT",
        "ticker@BTC_USDT"
    ]
}
```