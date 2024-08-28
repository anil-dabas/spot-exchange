# Spot WebSocket Service


## Features

- **WebSocket API:** Enables real-time, full-duplex communication between the client and the server.
- **Actuator:** Provides production-ready features to help you monitor and manage your application.

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Maven
- Kafka

## Getting Started

To run this Spring Boot application locally, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/workspaceai/spot-ws.git
   cd spot-ws
   
2. Build
    ````bash
   cd script
   ./build.sh
   
3. Run
    ````bash
    cd script
   ./run.sh
   
4. Run
    ````bash
    cd script
   ./run.sh
   
5. Health Checking with some Actuator APIs
    ````bash
    http://localhost:8080/actuator/health
    http://localhost:8080/actuator/metrics
    http://localhost:8080/actuator/info
6. API keys: add param: listen-key for header
   ```` json
   SBjWurSfX0JWJiNsiq3eluiCFxPReTAyvRV6i9ZTFCuchzogKV7P6nfcUkqVVOC6
   AZCaxxJbbnRAKOw8RUk0KYmms9fsv9R2SB3FWj1D8nG6MlPrlbqoA4OiCdkBmtAH
   
7. Swagger ui
   ````json
   http://localhost:30003/swagger-ui/index.html
  ````

### Import postman and play with its. Let's enjoy!!!
   
```json
Depth: matching.depth.updated
{
    "symbol": "BTC_USDT"
    "asks": [
      [
        "153.51",
        "160.67"
      ]
    ],
    "bids": [
      [
        "153.46",
        "32.96"
      ]
    ],
    "timestamp": 1715336392984
}
Trade: matching.order.matched
    {
	"symbol": "BTC_USDT",
	"buy": 1,
	"sell": 0,
	"quantity": "0.926",
	"price": "1",
	"is_buyer_maker": true,
	"timestamp": 1718968305680967000
}

topic: spot.market.ticker

{
        "first_price": "61017",
        "high": "63459.3",
        "last_price": "62955.5",
        "low": "60613",
        "price_change": "1938.5",
        "price_change_percent": "0.03177",
        "quote_volume": "1049666.01462",
        "symbol": "BTC_USDT",
        "trades": "3075",
        "volume": "16.89735"
}
Updated Order Event: spot.order.order.updated
{
  "id": 2072271355033796608,
  "user_id":"123456",
  "id_str": "2072271355033796608",
  "created_at": 1720086389349268000,
  "updated_at": 1720086389349268000,
  "symbol": "BTC_USDT",
  "type": "LIMIT",
  "side": "BUY",
  "price": "50",
  "quantity": "1",
  "quote_quantity": "0",
  "status": "NEW",
  "time_in_force": "GTC",
  "executed_quantity": "0",
  "executed_quote_quantity": "0"
}
Updated Account Event: spot.order.portfolio.updated
{
   "user_id": "123456",
   "assets": [
       {
           "asset_code": "BTC",
           "free": "0.9934",
           "locked": "0",
           "freeze": "0",
           "withdrawing": "0"
       },
       {
           "asset_code": "USDT",
           "free": "100.711",
           "locked": "81",
           "freeze": "0",
           "withdrawing": "0"
       }
   ]
}
````

### Links
https://www.baeldung.com/spring-boot-security-autoconfiguration
https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac
https://javacodehouse.com/blog/spring-boot-integration-test-gradle-setup/

