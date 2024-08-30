# This is a SPOT Exchange 

## Major services involved are 

- Market Data Service 
- Portfolio Service 
- Order Service 
- User Preference Service 
- Auth Service 
- Notification Service 
- Onboarding Service 
- Custody Service 
- Websocket Service

## The swagger endpoints are 

- Market Data Service started on http://localhost:8081/swagger-ui/index.html
- Portfolio Service started on http://localhost:8082/swagger-ui/index.html
- Order Service started on http://localhost:8083/swagger-ui/index.html
- User Preference Service started on http://localhost:8084/swagger-ui/index.html
- Auth Service started on http://localhost:8085/swagger-ui/index.html
- Notification Service started on http://localhost:8086/swagger-ui/index.html
- Onboarding Service started on http://localhost:8087/swagger-ui/index.html
- Custody Service started on http://localhost:8088/swagger-ui/index.html
- Websocket Service started http://localhost:8089/swagger-ui/index.html


## Order Service 

- The Order service is currently able to handle below (All this is authenticated using JWT)
  1. Place Order (Limit Order and Market Order for Buy and sell)
  2. Amend Order 
  3. Cancel Order 
- Other things to manage 
  1. Sends the orders to the matching engine using Kafka 
  2. Receives the response on Kafka topics for various possibilities like Order Matched, Rejected, Amended, cancelled
  3. Creates a response for the  user and publish to a kafka topic 
  4. Maintain the frozen balance of the user to check the eligibility if the user can further trade and freeze and unfreeze balance control 
  5. Ability to add pairs for listing and de-listing on the exchange 

## Asset Service 
- The asset service provides below 
  1. Add Balance 
  2. Get balance 
  3. Freeze and unfreeze balance 
  4. Transfer balance

## Auth Service 

- It is responsible for providing a basic authentication for all the services in this exchange with some hardcoded users 


## Details design for different services in scalable approach 

### SPOT Exchange Overview 

![img_2.png](img_2.png)

### SPOT Exchange Full detailed design

![img.png](img.png)

### OMS 

![img_3.png](img_3.png)

### Notification service

![img_1.png](img_1.png)

###