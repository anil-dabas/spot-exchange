package com.spot.order.service;

import com.spot.auth.model.CustomUserDetails;
import com.spot.order.cache.SnowflakeIdGenerator;
import com.spot.order.client.redis.RedisMarketDataClient;
import com.spot.order.client.rest.AssetServiceClient;
import com.spot.order.model.domain.Order;
import com.spot.order.model.domain.OrderRequest;
import com.spot.order.model.request.AmendOrderParam;
import com.spot.order.model.request.CancelOrderParam;
import com.spot.order.model.request.PlaceOrderParam;
import com.spot.order.model.response.OrderVo;
import com.spot.order.model.response.ResultVO;
import com.spot.order.repo.OrderRepository;
import com.spot.order.util.Constants;
import com.spot.order.model.domain.OrderState;
import com.spot.order.model.domain.OrderType;
import com.spot.order.util.TradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.spot.order.model.domain.OrderAction.*;
import static com.spot.order.model.domain.OrderSide.SELL;
import static com.spot.order.model.domain.OrderSide.BUY;
import static com.spot.order.util.Constants.*;

@Slf4j
@Service
public class TradeServiceImpl implements TradeService{


    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    RedisMarketDataClient redisMarketDataClient;

    @Autowired
    ModelMapper mapper;

    @Autowired
    AssetServiceClient assetServiceClient;

    SnowflakeIdGenerator orderIdGenerator = new SnowflakeIdGenerator(1, 1);
    SnowflakeIdGenerator requestIdGenerator = new SnowflakeIdGenerator(1, 1);

    @Override
    public ResultVO<OrderVo> placeOrder(PlaceOrderParam placeOrderParam, CustomUserDetails userDetails) {
        ResultVO<OrderVo> result = ResultVO.success();
        OrderRequest orderRequest = OrderRequest.builder().id(requestIdGenerator.nextId()).userId(userDetails.getUserId())
                .limitPrice(placeOrderParam.getLimitPrice()).createdAt(placeOrderParam.getCreatedAt())
                .instId(placeOrderParam.getInstId()).requestType(PLACE_ORDER.getValue()).validRequest(false)
                .side(placeOrderParam.getSide()).ordType(placeOrderParam.getOrdType()).build();


        // Filter the order correctness
        if (!orderService.validatePlaceOrderParams(placeOrderParam)) {
            orderService.submitIncomingRequest(orderRequest);
            result.setMsg(String.format("The order %s is not valid hence cannot be placed", orderRequest.getId()));
            return result;
        }

        BigDecimal orderQty = TradeUtil.toBigDecimal(placeOrderParam.getQuantity());
        String quoteCurrency = TradeUtil.getQuoteCurrency(placeOrderParam.getInstId());
        String baseCurrency = TradeUtil.getBaseCurrency(placeOrderParam.getInstId());

        // Buy Order
        if(BUY.getValue() == placeOrderParam.getSide()){
            // Check available balance if Buy Order
            BigDecimal avlQuoteBal = assetServiceClient.getBalanceForSymbol(quoteCurrency, userDetails.getUserId());
            BigDecimal requiredBalance = getRequiredBalanceInBigDecimal(placeOrderParam, orderQty);
            if(avlQuoteBal.compareTo(requiredBalance) < 0){
                orderService.submitIncomingRequest(orderRequest);
                result.setMsg(String.format("The user balance is insufficient for buy order %s", orderRequest.getId()));
                return result;
            }
            // Add to the freezeRecord
            boolean freezeApplied = orderService.freezeBalance(userDetails.getUserId(),quoteCurrency,requiredBalance, orderRequest.getId());
            if(!freezeApplied){
                orderService.submitIncomingRequest(orderRequest);
                result.setMsg(String.format("The order %s cannot be placed due insufficient balance please check available balance and try again", orderRequest.getId()));
                return result;
            }
            return createOrderObjectAndPlaceOrder(placeOrderParam, orderRequest);
        }
        // Sell Order
        if(SELL.getValue() == placeOrderParam.getSide()){
            BigDecimal avlBaseQty = assetServiceClient.getBalanceForSymbol(baseCurrency, userDetails.getUserId() );
            if (avlBaseQty.compareTo(orderQty) < 0) {
                result.setMsg(String.format("The user quantity is insufficient for sell order %s", orderRequest.getId()));
                return result;
            }

            // Freeze quantity for a Sell Order
            boolean freezeApplied = orderService.freezeBalance(userDetails.getUserId(), baseCurrency, orderQty, orderRequest.getId());
            if (!freezeApplied) {
                result.setMsg(String.format("The order %s cannot be placed due to issue in available balance please check available balance and try again",orderRequest.getId()));
                return result;
            }
            return createOrderObjectAndPlaceOrder(placeOrderParam, orderRequest);
        }
        return result;
    }

    @Override
    public ResultVO<OrderVo> amendOrder(AmendOrderParam amendOrderParam, CustomUserDetails userDetails) {
        OrderRequest orderRequest = OrderRequest.builder().id(requestIdGenerator.nextId()).orderId(amendOrderParam.getOrderId())
                .validRequest(false).requestType(AMEND_ORDER.getValue()).quantity(amendOrderParam.getQuantity())
                .createdAt(amendOrderParam.getCreatedAt()).userId(userDetails.getUserId()).build();
        ResultVO<OrderVo> result;
        Order order = orderRepository.findByOrderId(amendOrderParam.getOrderId());
        result = orderService.checkAmendOrderValidity(amendOrderParam, order);
        if(FAILURE == result.getCode()){
            orderService.submitIncomingRequest(orderRequest);
            return  result;
        }
        if(StringUtils.isNotEmpty(amendOrderParam.getLimitPrice()) && StringUtils.isNotEmpty(amendOrderParam.getQuantity())){
            BigDecimal newLimitPrice = TradeUtil.toBigDecimal(amendOrderParam.getLimitPrice());
            BigDecimal amendQty = TradeUtil.toBigDecimal(amendOrderParam.getQuantity());

            // Check order Eligibility
            boolean isAmendEligible = orderService.checkOrderAmendPrerequisites(newLimitPrice, amendQty,order,userDetails,orderRequest.getId());
            if(!isAmendEligible){
                result.setCode(Constants.FAILURE);
                result.setMsg("The amend order failed due to insufficient funds");
                orderService.submitIncomingRequest(orderRequest);
                return  result;
            }
        }else if(StringUtils.isNotEmpty(amendOrderParam.getLimitPrice())
                && TradeUtil.toBigDecimal(amendOrderParam.getLimitPrice()).compareTo(order.getLimitPrice()) >0){
            BigDecimal newLimitPrice = TradeUtil.toBigDecimal(amendOrderParam.getLimitPrice());
            BigDecimal orderQty = TradeUtil.toBigDecimal(order.getQuantity());

            // Check Order Eligibility
            boolean isAmendEligible = orderService.checkOrderAmendPrerequisites(newLimitPrice, orderQty,order, userDetails,orderRequest.getId());
            if(!isAmendEligible){
                result.setCode(Constants.FAILURE);
                result.setMsg("The amend order failed due to insufficient funds");
                orderService.submitIncomingRequest(orderRequest);
                return  result;
            }
        }
        return updateAmendOrderAndPlaceInQueue(amendOrderParam, order,orderRequest);

    }


    private BigDecimal getRequiredBalanceInBigDecimal(PlaceOrderParam placeOrderParam, BigDecimal orderQty) {
        BigDecimal requiredBalance = new BigDecimal("0");
        if(OrderType.LIMIT.equals(placeOrderParam.getOrdType())){
            requiredBalance = orderQty.multiply(TradeUtil.toBigDecimal(placeOrderParam.getLimitPrice()));
        }else if(OrderType.MARKET.equals(placeOrderParam.getOrdType())){
            double latestPrice = redisMarketDataClient.getLatestInstrumentData(placeOrderParam.getInstId());
            requiredBalance = MARKET_MARGIN_BUFFER.multiply(orderQty.multiply(new BigDecimal(latestPrice)));
        }
        return requiredBalance;
    }

    private ResultVO<OrderVo> createOrderObjectAndPlaceOrder(PlaceOrderParam placeOrderParam, OrderRequest orderRequest) {
        ResultVO<OrderVo> result = ResultVO.success();
        Order order = Order.builder()
                .createdAt(placeOrderParam.getCreatedAt())
                .requestId(orderRequest.getId())
                .side(placeOrderParam.getSide())
                .orderType(placeOrderParam.getOrdType().getValue())
                .timestamp(System.currentTimeMillis())
                .orderId(orderIdGenerator.nextId())
                .quantity(placeOrderParam.getQuantity())
                .instId(placeOrderParam.getInstId())
                .userId(orderRequest.getUserId())
                .state(OrderState.PENDING)
                .build();
        if(OrderType.LIMIT.getValue() == placeOrderParam.getSide()){
            log.info("Place Order : limit price for the order is {}" , placeOrderParam.getLimitPrice());
            order.setLimitPrice(TradeUtil.toBigDecimal(placeOrderParam.getLimitPrice()));
        }
        log.info("Place Order : Placed the order is {}" , order);
        order = orderRepository.save(order);
        orderService.submitOrderRequestToQueue(order,orderRequest);
        orderRequest.setOrderId(order.getOrderId());
        orderRequest.setValidRequest(true);
        orderService.submitIncomingRequest(orderRequest);
        result.setMsg("Place order request submitted successfully");
        result.setData(List.of(mapper.map(order, OrderVo.class)));
        return result;
    }

    private ResultVO<OrderVo> updateAmendOrderAndPlaceInQueue(AmendOrderParam amendOrderParam, Order order, OrderRequest orderRequest) {
        ResultVO<OrderVo> result = ResultVO.success();
        order.setQuantity(amendOrderParam.getQuantity());
        orderRequest.setValidRequest(true);
        orderService.submitIncomingRequest(orderRequest);
        result.setMsg("Order Amended request successfully submitted");
        result.setData(List.of(mapper.map(order, OrderVo.class)));
        orderService.submitOrderRequestToQueue(order,orderRequest);
        return result;
    }

    @Override
    public ResultVO<OrderVo> cancelOrder(CancelOrderParam cancelOrderParam, CustomUserDetails userDetails) {
        ResultVO<OrderVo> result = ResultVO.success();
        OrderRequest orderRequest = OrderRequest.builder().id(requestIdGenerator.nextId()).requestType(CANCEL_ORDER.getValue()).orderId(cancelOrderParam.getOrdId())
                .userId(userDetails.getUserId()).validRequest(false).createdAt(cancelOrderParam.getCreatedAt()).ordType(cancelOrderParam.getOrdType()).instId(cancelOrderParam.getInstId()).build();

        Order order = orderRepository.findByOrderId(cancelOrderParam.getOrdId());
        if(order == null){
            result.setMsg(String.format("Order with order Id %s not available please check and try cancel order again ", cancelOrderParam.getOrdId()));
            orderService.submitIncomingRequest(orderRequest);
            return result;
        }
        if(!OrderState.PENDING.equals(order.getState())){
            result.setMsg(String.format("The order with OrderId %s is already ", order.getState()));
            orderService.submitIncomingRequest(orderRequest);
            return result;
        }
        orderService.submitOrderRequestToQueue(order, orderRequest);
        orderRequest.setValidRequest(true);
        orderService.submitIncomingRequest(orderRequest);
        result.setMsg(String.format("The order with OrderId %s is successfully requested for cancellation ", order.getOrderId()));
        return result;
    }

}
