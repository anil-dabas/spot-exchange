This Project is to generate the kline/OHLC data from the trade information.

Input: Read the trade data in below format from Queue (generated from Matching Engine)

tradeData :  {
             	"symbol": "BTC_USDT",
             	"buy": 1,
             	"sell": 0,
             	"quantity": "0.926",
             	"price": "1",
             	"trade_by": "BUYER",
             	"timestamp": 1718968305680967000
             }

Output :  Write the OHLC data to Queue and Redis in below format.
Supported kline intervals (case-sensitive):

Interval	interval value
seconds	1s
minutes	1m, 3m, 5m, 15m, 30m
hours	1h, 2h, 4h, 6h, 8h, 12h
days	1d, 3d
weeks	1w
months	1M

[
  [
    1499040000000,      // Kline open time
    "0.01634790",       // Open price
    "0.80000000",       // High price
    "0.01575800",       // Low price
    "0.01577100",       // Close price
    "148976.11427815",  // Volume
    1499644799999,      // Kline Close time
    "2434.19055334",    // Quote asset volume
    308,                // Number of trades
    "1756.87402397",    // Taker buy base asset volume
    "28.46694368",      // Taker buy quote asset volume
    "0"                 // Unused field, ignore.
  ]
]
