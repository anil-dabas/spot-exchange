package com.spot.order.service;

import com.spot.auth.model.CustomUserDetails;
import com.spot.order.client.rest.AssetServiceClient;
import com.spot.order.model.domain.*;
import com.spot.order.model.payload.listener.AmendedOrderUpdate;
import com.spot.order.model.payload.listener.CanceledOrderUpdate;
import com.spot.order.model.payload.listener.ExpiredOrderUpdate;
import com.spot.order.model.payload.listener.RejectedOrderUpdate;
import com.spot.order.model.payload.publisher.OrderRequestPayload;
import com.spot.order.model.payload.publisher.UpdateOrderPayload;
import com.spot.order.model.request.AmendOrderParam;
import com.spot.order.model.request.PlaceOrderParam;
import com.spot.order.model.request.QueryOrderParam;
import com.spot.order.model.response.OrderVo;
import com.spot.order.model.response.ResultVO;
import com.spot.order.repo.*;
import com.spot.order.kafka.KafkaOrderPublisher;
import com.spot.order.util.Constants;
import com.spot.order.cache.ListedPairsCache;
import com.spot.order.model.domain.*;
import com.spot.order.repo.*;
import com.spot.order.util.OrderUtils;
import com.spot.order.util.TradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.spot.order.model.domain.OrderSide.BUY;
import static com.spot.order.util.Constants.DUMMY_ORDER_ID;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    KafkaOrderPublisher orderPublisher;

    @Autowired
    AssetServiceClient assetServiceClient;

    @Autowired
    FreezeRepository freezeRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderRequestRepository orderRequestRepository;

    @Autowired
    OrderFinishRepository orderFinishRepository;

    @Autowired
    OrderFillRepository orderFillRepository;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public  OrderRequest submitIncomingRequest(OrderRequest orderRequest){
        return orderRequestRepository.save(orderRequest);
    }

    @Override
    public boolean validatePlaceOrderParams(PlaceOrderParam placeOrderParam) {
        if(!ListedPairsCache.listedPairs.contains(placeOrderParam.getInstId())){
            return false;
        }
        return true;
    }

    public boolean freezeBalance(Long userId, String quoteCurrency, BigDecimal requiredBalance, Long requestId) {
        boolean freezeSuccessful = assetServiceClient.freezeBalance(quoteCurrency,requiredBalance,userId );
        if (freezeSuccessful) {
            FreezeRecord freezeRecord = FreezeRecord.builder()
                    .requestId(requestId)
                    .createdAt(LocalDateTime.now())
                    .frozenBalance(requiredBalance)
                    .userId(userId)
                    .currencySymbol(quoteCurrency).build();
            freezeRepository.save(freezeRecord);
        }
        return freezeSuccessful;
    }

    @Override
    public ResultVO<OrderVo> checkAmendOrderValidity(AmendOrderParam amendOrderParam, Order order) {
        ResultVO<OrderVo> result = ResultVO.success();
        if(order == null){
            result.setCode(Constants.FAILURE);
            result.setMsg(String.format("Order with order Id %s not available please check and try cancel order again ", amendOrderParam.getOrderId()));
            return  result;
        }
        if(StringUtils.isNotEmpty(amendOrderParam.getInstId()) && !order.getInstId().equals(amendOrderParam.getInstId())){
            result.setCode(Constants.FAILURE);
            result.setMsg("The amend order failed as the Instrument in the actual order does not match the amend order");
            return  result;
        }
        if(StringUtils.isEmpty(amendOrderParam.getQuantity()) && StringUtils.isEmpty(amendOrderParam.getLimitPrice())){
            result.setCode(Constants.FAILURE);
            result.setMsg("The amend order failed as either the quantity or the limit price should be available");
            return  result;
        }
        if(StringUtils.isNotEmpty(amendOrderParam.getQuantity())){
            BigDecimal orderQty = TradeUtil.toBigDecimal(order.getQuantity());
            BigDecimal amendQty = TradeUtil.toBigDecimal(amendOrderParam.getQuantity());
            if(orderQty.compareTo(amendQty) < 0){
                result.setCode(Constants.FAILURE);
                result.setMsg("The amend order failed as the amended qty is grater than qty in the actual order we don't support increased qty amend");
                return  result;
            }
        }
        return result;
    }

    @Override
    public boolean updateFreezeBalance(Long userId, String quoteCurrency, BigDecimal requiredBalance, Long requestId) {
        boolean updateFreezeSuccess = assetServiceClient.freezeBalance(quoteCurrency,requiredBalance, userId);
        if(updateFreezeSuccess){
            FreezeRecord freezeRecord = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(requestId, userId,quoteCurrency, true);
            freezeRecord.setFrozenBalance(freezeRecord.getFrozenBalance().multiply(requiredBalance));
            freezeRepository.save(freezeRecord);
        }
        return updateFreezeSuccess;
    }

    @Override
    public void submitOrderRequestToQueue(Order order, OrderRequest orderRequest) {
        // Need to map properly
        OrderRequestPayload payload = OrderRequestPayload.builder().id(order.getRequestId().toString())
                .orderAction(orderRequest.getRequestType()).orderType(order.getOrderType()).orderId(order.getOrderId())
                .side(order.getSide()).quantity(order.getQuantity()).quoteQuantity(order.getQuoteQuantity())
                .createdAt(TradeUtil.convertToMicroseconds(orderRequest.getCreatedAt()))
                .price(order.getOrderType() == OrderType.LIMIT.getValue() ? orderRequest.getLimitPrice() : null).build();
        log.info("Publish to kafka : Instrument id {} OrderPayload {} ",order.getInstId(),payload);
        orderPublisher.publishOrder(payload, order.getInstId());
    }

    @Override
    public void updateMatchedOrderDetailsAndPublishResponse(TradeResponse tradeResponse) {
        // Finding Order details involved in the trade.
        log.info("Match Order : The trade response received is  {}",tradeResponse);
        Order buyOrder = orderRepository.findByOrderId(tradeResponse.getBuy());
        Order sellOrder = orderRepository.findByOrderId(tradeResponse.getSell());
        log.info("Match Order : The buy order matched is {} & Sell order matched is {}",buyOrder, sellOrder);
        // Saving Order Fills
        List<OrderFill> orderFills = createOrderFillsForTrade(tradeResponse,buyOrder,sellOrder);
        log.info("Match Order : Saving the order fill {} ",orderFills);
        orderFillRepository.saveAll(orderFills);

        LocalDateTime timeOfExecution = TradeUtil.convertMicrosecondsToLocalDateTime(tradeResponse.getTimestamp());

        // Updating buy Order
        buyOrder.setUpdatedAt(timeOfExecution);
        BigDecimal updatedBuyExecuted = TradeUtil.toBigDecimal(buyOrder.getExecutedQuantity()).add(TradeUtil.toBigDecimal(tradeResponse.getQuantity()));
        buyOrder.setExecutedQuantity(TradeUtil.toStringFromBigDecimal(updatedBuyExecuted));
        buyOrder.setState(OrderState.PARTIALLY_FILLED);



        // Updating sell Order
        sellOrder.setUpdatedAt(timeOfExecution);
        BigDecimal updatedSellExecuted = TradeUtil.toBigDecimal(sellOrder.getExecutedQuantity()).add(TradeUtil.toBigDecimal(tradeResponse.getQuantity()));
        sellOrder.setExecutedQuantity(TradeUtil.toStringFromBigDecimal(updatedSellExecuted));
        sellOrder.setState(OrderState.PARTIALLY_FILLED);

        // call Transfer after matching the order
        String quoteCurrency = TradeUtil.getQuoteCurrency(buyOrder.getInstId());
        String baseCurrency = TradeUtil.getBaseCurrency(sellOrder.getInstId());

        log.info("Initiating Balance transfer");
        BigDecimal transferBuy = TradeUtil.toBigDecimal(tradeResponse.getQuantity()).multiply(TradeUtil.toBigDecimal(tradeResponse.getPrice()));
        BigDecimal transferSell = TradeUtil.toBigDecimal(tradeResponse.getQuantity());

        FreezeRecord freezeRecordBuy = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(buyOrder.getRequestId(), buyOrder.getUserId(), quoteCurrency,true);
        freezeRecordBuy.setFrozenBalance(freezeRecordBuy.getFrozenBalance().subtract(transferBuy));
        freezeRecordBuy.setUpdatedAt(LocalDateTime.now());
        freezeRecordBuy.setValidFreeze(freezeRecordBuy.getFrozenBalance().compareTo(BigDecimal.ZERO) != 0);
        FreezeRecord freezeRecordSell = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(sellOrder.getRequestId(), sellOrder.getUserId(), quoteCurrency,true);
        freezeRecordSell.setFrozenBalance(freezeRecordSell.getFrozenBalance().subtract(transferSell));
        freezeRecordSell.setValidFreeze(freezeRecordSell.getFrozenBalance().compareTo(BigDecimal.ZERO) != 0);
        freezeRecordSell.setUpdatedAt(LocalDateTime.now());

        freezeRepository.saveAll(List.of(freezeRecordSell,freezeRecordBuy));

        if(!(assetServiceClient.transferBalance(buyOrder.getUserId(), sellOrder.getUserId(), quoteCurrency, transferBuy) &&
            assetServiceClient.transferBalance(sellOrder.getUserId(), buyOrder.getUserId(), baseCurrency , transferSell))){
            //Save the failed order requests  here
            log.info("Balance transfer failed");
        }else{
            log.info("Balance completed");
        }

        // Save orders
        log.info("Match Order : Saving updated orders in the order table");
        orderRepository.saveAll(List.of(buyOrder,sellOrder));

        // Save Finished Orders
        if(TradeUtil.toBigDecimal(buyOrder.getQuantity()).compareTo(TradeUtil.toBigDecimal(buyOrder.getExecutedQuantity())) ==0){
            createAndSaveFinishedOrder(buyOrder);
        }
        if(TradeUtil.toBigDecimal(sellOrder.getQuantity()).compareTo(TradeUtil.toBigDecimal(sellOrder.getExecutedQuantity())) ==0){
            createAndSaveFinishedOrder(sellOrder);
        }

        // Convert to UpdatedOrderPayload Buy and Sell Order
        UpdateOrderPayload updateBuyOrderPayload = OrderUtils.createUpdateOrderPayloadFromOrder(buyOrder);
        UpdateOrderPayload updateSellOrderPayload = OrderUtils.createUpdateOrderPayloadFromOrder(sellOrder);

        log.info("Match Order : Publishing buy order to update order kafka {}",updateBuyOrderPayload);
        orderPublisher.publishOrderUpdate(updateBuyOrderPayload);
        log.info("Match Order : Publishing sell order to update order kafka {}",updateSellOrderPayload);
        orderPublisher.publishOrderUpdate(updateSellOrderPayload);

    }

    private void createAndSaveFinishedOrder(Order order) {
        order.setState(OrderState.FILLED);
        OrderFinish buyOrderFinish = OrderUtils.createOrderFinishFromOrder(order);
        log.info("Match Order : Saving in finished order table");
        orderFinishRepository.save(buyOrderFinish);
    }

    private List<OrderFill> createOrderFillsForTrade(TradeResponse tradeResponse, Order buyOrder, Order sellOrder) {
        OrderFill buyOrderFill = OrderFill.builder()
                .tradeId(tradeResponse.getId())
                .instId(buyOrder.getInstId())
                .orderId(tradeResponse.getBuy())
                .filledQty(tradeResponse.getQuantity())
                .orderSide(buyOrder.getSide())
                .matchedAt(TradeUtil.convertMicrosecondsToLocalDateTime(tradeResponse.getTimestamp()))
                .timestamp(tradeResponse.getTimestamp())
                .orderType(buyOrder.getOrderType()).build();

        OrderFill sellOrderFill = OrderFill.builder()
                .tradeId(tradeResponse.getId())
                .orderId(tradeResponse.getSell())
                .instId(sellOrder.getInstId())
                .filledQty(tradeResponse.getQuantity())
                .orderSide(sellOrder.getSide())
                .matchedAt(TradeUtil.convertMicrosecondsToLocalDateTime(tradeResponse.getTimestamp()))
                .timestamp(tradeResponse.getTimestamp())
                .orderType(sellOrder.getOrderType()).build();
        return List.of(buyOrderFill,sellOrderFill);
    }

    @Override
    public ResultVO<OrderVo> queryOrder(Long orderId, Long userId) {
        ResultVO<OrderVo> result = ResultVO.success();
        Order order = orderRepository.findByIdAndUserId(orderId,userId).orElse(Order.builder().orderId(-1).build());
        if(DUMMY_ORDER_ID == orderId){
            result.setMsg(String.format("Order with order Id %s not available please check and try again ", orderId));
            result.setData(List.of());
            return result;
        }
        OrderVo orderVo = modelMapper.map(order,OrderVo.class);
        result.setData(List.of(orderVo));
        result.setMsg(String.format("Order with order Id %s retrieved successfully ", orderId));
        return result;
    }

    @Override
    public ResultVO<OrderVo> queryUnfinishedOrders(QueryOrderParam queryOrderParam, Long userId) {
        log.info("Query Order : Querying the pending orders with query params {}",queryOrderParam);
        ResultVO<OrderVo> result = ResultVO.success();
        List<Order> orders = orderRepository.findAllByStateAndUserId(OrderState.PENDING,userId);
        log.info("Query Order : All orders with pending state {}" ,orders);
        List<OrderVo> orderVos = orders.stream().filter(l -> filterOrders(l,queryOrderParam, true)).map(l -> modelMapper.map(l,OrderVo.class)).toList();
        log.info("Query Order : filtered orders with pending state {}",orderVos);
        result.setData(orderVos);
        return result;
    }


    @Override
    public ResultVO<OrderVo> queryHistoryOrder(QueryOrderParam queryOrderParam, Long userId) {
        ResultVO<OrderVo> result = ResultVO.success();
        List<Order> orders = orderRepository.findAllByUserId(userId);
        result.setData(orders.stream().filter(l -> filterOrders(l,queryOrderParam, false)).map( l -> modelMapper.map(l,OrderVo.class)).collect(Collectors.toList()));
        return result;
    }

    @Override
    public boolean checkOrderAmendPrerequisites(BigDecimal limitPrice, BigDecimal qty, Order order, CustomUserDetails userDetails, Long requestId) {
        String currency = BUY.getValue() == order.getSide() ? TradeUtil.getQuoteCurrency(order.getInstId()) : TradeUtil.getBaseCurrency(order.getInstId());
        if (BUY.getValue() == order.getSide()) {
            BigDecimal requiredBalance = qty.multiply(order.getLimitPrice());
            BigDecimal freezeApplied = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(order.getRequestId(),userDetails.getUserId(),currency, true).getFrozenBalance();
            if(freezeApplied.compareTo(requiredBalance) >=0){
                if(freezeApplied.compareTo(requiredBalance) > 0){
                    BigDecimal requiredFreeze = requiredBalance.subtract(freezeApplied);
                    freezeBalance(userDetails.getUserId(), currency,requiredFreeze,requestId);
                }
                return true;
            }else{
                BigDecimal requiredFreeze = requiredBalance.subtract(freezeApplied);
                BigDecimal avlBalance = assetServiceClient.getBalanceForSymbol(currency, userDetails.getUserId());
                if(avlBalance.compareTo(requiredFreeze) >=0 && freezeBalance(userDetails.getUserId(), currency,requiredFreeze,requestId)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updateCancelledOrderToQueue(CanceledOrderUpdate orderStatus) {
        Order order = orderRepository.findByOrderId(orderStatus.getOrderId());
        log.info("Cancel Order : The cancel order request for order {} is accepted ", order);
        log.info("Cancel Order : The cancel order status from kafka {} ", orderStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order.setState(OrderState.CANCELLED);
        String currencySymbol = BUY.getValue() == order.getSide() ? TradeUtil.getQuoteCurrency(order.getInstId()) : TradeUtil.getBaseCurrency(order.getInstId());
        FreezeRecord freezeRecord = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(order.getRequestId(),order.getUserId(),currencySymbol, true);
        log.info("Cancel Order : Frozen amount for the order {}",freezeRecord);
        boolean froze = assetServiceClient.unFreezeBalance(freezeRecord.getCurrencySymbol(),freezeRecord.getFrozenBalance(), freezeRecord.getUserId());
        log.info("Cancel Order : Froze status {}",froze);
        if(froze){
            log.info("Cancel Order : Amount successfully unfreeze");
            freezeRecord.setValidFreeze(false);
            freezeRepository.save(freezeRecord);
            // Create the UpdateOrderPayload here and push to Kafka queue
            UpdateOrderPayload updateOrderPayload = OrderUtils.createUpdateOrderPayloadFromOrder(order);
            log.info("Cancel Order : Publishing cancel order {} to the queue",order);
            orderPublisher.publishOrderUpdate(updateOrderPayload);
            orderRepository.save(order);
        }

    }

    @Override
    public void updateExpiredOrderToQueue(ExpiredOrderUpdate orderStatus) {
        Order order = orderRepository.findByOrderId(orderStatus.getOrderId());
        log.info("Expired Order : Order expired {}",order);
        order.setState(OrderState.EXPIRED);
        String currencySymbol = BUY.getValue() == order.getSide() ? TradeUtil.getQuoteCurrency(order.getInstId()) : TradeUtil.getBaseCurrency(order.getInstId());
        FreezeRecord freezeRecord = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(order.getRequestId(),order.getUserId(),currencySymbol, true);
        log.info("Expired Order : Freeze Record for the expired order {}", freezeRecord);
        boolean froze = assetServiceClient.unFreezeBalance(freezeRecord.getCurrencySymbol(),freezeRecord.getFrozenBalance(), freezeRecord.getUserId() );
        if(froze){
            log.info("Expired Order : Amount unfreeze");
            freezeRecord.setValidFreeze(false);
            freezeRepository.save(freezeRecord);
            // Create the UpdateOrderPayload here and push to Kafka queue
            UpdateOrderPayload updateOrderPayload = OrderUtils.createUpdateOrderPayloadFromOrder(order);
            log.info("Expired Order : Publishing Order Update successfully on queue {}",updateOrderPayload);
            orderPublisher.publishOrderUpdate(updateOrderPayload);
            orderRepository.save(order);
        }
    }

    @Override
    public void updateRejectedOrderToQueue(RejectedOrderUpdate orderStatus) {
        Order order = orderRepository.findByOrderId(orderStatus.getOrderId());
        log.info("Rejected Order            : Order rejected with order Id {}",order.getOrderId());
        OrderRequest orderRequest = orderRequestRepository.findById(Long.parseLong(orderStatus.getId())).get();
        String currencySymbol = BUY.getValue() == order.getSide() ? TradeUtil.getQuoteCurrency(order.getInstId()) : TradeUtil.getBaseCurrency(order.getInstId());
        FreezeRecord freezeRecordOriginalOrder = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(order.getRequestId(),order.getUserId(),currencySymbol, true);

        switch (RejectType.fromValue(orderStatus.getRejectType())) {
            case AMEND_REJECT -> handleAmendReject(order,currencySymbol,orderRequest);
            case CANCEL_REJECT -> handleCancelReject(orderRequest);
            case PLACE_ORDER_REJECT -> handlePlaceOrderReject(currencySymbol,freezeRecordOriginalOrder,order);
            default -> throw new IllegalArgumentException("Unknown RejectType: " + orderStatus.getRejectType());
        }
    }

    private void handlePlaceOrderReject(String currencySymbol, FreezeRecord freezeRecordOriginalOrder, Order order) {
        assetServiceClient.unFreezeBalance(currencySymbol, freezeRecordOriginalOrder.getFrozenBalance(), freezeRecordOriginalOrder.getUserId());
        freezeRecordOriginalOrder.setValidFreeze(false);
        freezeRepository.save(freezeRecordOriginalOrder);
        order.setState(OrderState.REJECTED);
        UpdateOrderPayload updateOrderPayload = OrderUtils.createUpdateOrderPayloadFromOrder(order);
        orderPublisher.publishOrderUpdate(updateOrderPayload);
        orderRepository.save(order);
    }

    private void handleCancelReject(OrderRequest orderRequest) {
        UpdateOrderPayload updateOrderPayload = OrderUtils.createUpdateOrderPayloadFromOrderRequest(orderRequest);
        orderPublisher.publishOrderUpdate(updateOrderPayload);
    }

    private void handleAmendReject(Order order, String currencySymbol, OrderRequest orderRequest) {
        FreezeRecord freezeRecordOrdReq = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(order.getRequestId(), order.getUserId(), currencySymbol, true);
        if(freezeRecordOrdReq !=null){
            if(freezeRecordOrdReq.getFrozenBalance().compareTo(BigDecimal.ZERO) > 0){
                assetServiceClient.unFreezeBalance(currencySymbol,freezeRecordOrdReq.getFrozenBalance(), freezeRecordOrdReq.getUserId());
            }
            freezeRecordOrdReq.setValidFreeze(false);
            freezeRepository.save(freezeRecordOrdReq);
        }
        UpdateOrderPayload updateOrderPayload = OrderUtils.createUpdateOrderPayloadFromOrderRequest(orderRequest);
        orderPublisher.publishOrderUpdate(updateOrderPayload);
    }

    @Override
    public void updateAmendedOrderToQueue(AmendedOrderUpdate orderStatus) {
        Order order = orderRepository.findByOrderId(orderStatus.getOrderId());
        log.info("Amended Order            : Order amended with order Id {}",order.getOrderId());
        OrderRequest orderRequest = orderRequestRepository.findById(orderStatus.getId()).orElse(null);
        if(order.getSide() == orderStatus.getSide()){
            String currencySymbol = BUY.getValue() == order.getSide() ? TradeUtil.getQuoteCurrency(order.getInstId()) : TradeUtil.getBaseCurrency(order.getInstId());
            FreezeRecord freezeRecordForAmend = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(orderStatus.getId(), order.getUserId(), currencySymbol, true);
            FreezeRecord freezeRecordForOrder = freezeRepository.findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(order.getRequestId(), order.getUserId(), currencySymbol, true);

            log.info("Amend Order : The freeze for original order is {}",freezeRecordForOrder);
            log.info("Amend Order : The freeze for amend if any is {}",freezeRecordForAmend);

            if(freezeRecordForAmend != null){
                log.info("Amend Order : freeze for amend available ");
                if(freezeRecordForAmend.getFrozenBalance().compareTo(BigDecimal.ZERO) <0){
                    BigDecimal unfreeZeBal = BigDecimal.valueOf(-1).multiply(freezeRecordForAmend.getFrozenBalance());
                log.info("Amend Order : unfreeze the amended freeze ");
                    assetServiceClient.unFreezeBalance(currencySymbol,unfreeZeBal, freezeRecordForAmend.getUserId());
                }
                freezeRecordForOrder.setFrozenBalance(freezeRecordForOrder.getFrozenBalance().add(freezeRecordForAmend.getFrozenBalance()));
                freezeRecordForOrder.setUpdatedAt(LocalDateTime.now());
                freezeRecordForAmend.setValidFreeze(false);
                freezeRepository.saveAll(List.of(freezeRecordForAmend,freezeRecordForOrder));
            }else{
                log.info("Amend Order : freeze for amend is not available ");
                BigDecimal limitPrice = (orderRequest != null && orderRequest.getLimitPrice()!= null && TradeUtil.toBigDecimal(orderRequest.getLimitPrice()).compareTo(BigDecimal.ZERO)>0) ? TradeUtil.toBigDecimal(orderRequest.getLimitPrice()) : order.getLimitPrice();
                BigDecimal quantity = (orderRequest != null && orderRequest.getQuantity() != null && TradeUtil.toBigDecimal(orderRequest.getQuantity()).compareTo(BigDecimal.ZERO)>0)? TradeUtil.toBigDecimal(orderRequest.getQuantity()) : TradeUtil.toBigDecimal(order.getQuantity());
                log.info("Amend Order : Limit price is {} and quantity is {} ",limitPrice,quantity );
                BigDecimal requiredFreeze = limitPrice.multiply(quantity);
                log.info("Amend Order : Freeze balance required for amend is {}",requiredFreeze );
                BigDecimal actualFreeze = freezeRecordForOrder.getFrozenBalance();
                log.info("Amend Order : Actual Freeze done when order was placed {}",actualFreeze );
                BigDecimal refund = actualFreeze.subtract(requiredFreeze);
                // call unfreeze for refund
                log.info("Amend Order : Refund the extra amount  {}",refund );
                assetServiceClient.unFreezeBalance(currencySymbol,refund, order.getUserId());
                // save actual freeze
                freezeRecordForOrder.setFrozenBalance(actualFreeze);
                freezeRepository.save(freezeRecordForOrder);
            }
            order.setUpdatedAt(LocalDateTime.now());
            order.setQuantity(orderStatus.getNewQuantity());
            orderRepository.save(order);
            UpdateOrderPayload updateOrderPayload = OrderUtils.createUpdateOrderPayloadFromOrder(order);
            log.info("Amended Order : Amended order update published to order update queue");
            orderPublisher.publishOrderUpdate(updateOrderPayload);
        }

    }


    private boolean filterOrders(Order order, QueryOrderParam queryOrderParam, boolean isUnfinishedOrdersRequest) {
        // Check if the query is for unfinished orders and match the order state accordingly
        log.info("Query Order filter: The filtering of order with order {} on the basis of params {}",order,queryOrderParam);

        if ((isUnfinishedOrdersRequest && !order.getState().equals(OrderState.PENDING)) ||
                (!isUnfinishedOrdersRequest && order.getState().equals(OrderState.PENDING))) {
            return false;
        }
        // Filter based on orderType if set
        if (queryOrderParam.getOrderType() != null && order.getOrderType() != queryOrderParam.getOrderType().getValue()) {
            return false;
        }

        // Filter based on instId if set
        if (queryOrderParam.getInstId() != null && !queryOrderParam.getInstId().equals(order.getInstId())) {
            return false;
        }

        // Filter based on state if set
        if (queryOrderParam.getState() != null && order.getState() != queryOrderParam.getState()) {
            return false;
        }

        // Filter based on filterCancel flag
        if (queryOrderParam.isFilterCancel() && order.getState().equals(OrderState.CANCELLED)) {
            return false;
        }

        // If allOrders is false and orderIds is set, check if the orderId is in the list
        if (!queryOrderParam.isAllOrders() && queryOrderParam.getOrderIds() != null && !queryOrderParam.getOrderIds().isEmpty()) {
            if (!queryOrderParam.getOrderIds().contains(order.getOrderId())) {
                return false;
            }
        }
        // If all checks pass, the order matches the filter criteria
        return true;
    }

}
