package com.spot.order.service;

import com.spot.auth.model.CustomUserDetails;
import com.spot.order.model.domain.*;
import com.spot.order.model.domain.Order;
import com.spot.order.model.domain.OrderRequest;
import com.spot.order.model.payload.listener.AmendedOrderUpdate;
import com.spot.order.model.payload.listener.CanceledOrderUpdate;
import com.spot.order.model.payload.listener.ExpiredOrderUpdate;
import com.spot.order.model.payload.listener.RejectedOrderUpdate;
import com.spot.order.model.request.AmendOrderParam;
import com.spot.order.model.request.PlaceOrderParam;
import com.spot.order.model.request.QueryOrderParam;
import com.spot.order.model.response.OrderVo;
import com.spot.order.model.response.ResultVO;
import com.spot.order.model.domain.TradeResponse;

import java.math.BigDecimal;

public interface OrderService {
    OrderRequest submitIncomingRequest(OrderRequest orderRequest);

    boolean validatePlaceOrderParams(PlaceOrderParam placeOrderParam) ;

    void submitOrderRequestToQueue(Order order, OrderRequest placeOrder);

    boolean freezeBalance(Long userId, String quoteCurrency, BigDecimal requiredBalance, Long requestId);

    ResultVO<OrderVo> checkAmendOrderValidity(AmendOrderParam amendOrderParam, Order order);

    boolean updateFreezeBalance(Long userId, String quoteCurrency, BigDecimal requiredBalance, Long requestId);

    void updateMatchedOrderDetailsAndPublishResponse(TradeResponse tradeResponse);

    ResultVO<OrderVo> queryOrder(Long orderId, Long userId);

    ResultVO<OrderVo> queryUnfinishedOrders(QueryOrderParam queryOrderParam, Long userId);

    ResultVO<OrderVo> queryHistoryOrder(QueryOrderParam queryOrderParam, Long userId);

    boolean checkOrderAmendPrerequisites(BigDecimal newLimitPrice, BigDecimal amendQty, Order order, CustomUserDetails userDetails, Long id);

    void updateCancelledOrderToQueue(CanceledOrderUpdate orderStatus);

    void updateExpiredOrderToQueue(ExpiredOrderUpdate orderStatus);

    void updateRejectedOrderToQueue(RejectedOrderUpdate orderStatus);

    void updateAmendedOrderToQueue(AmendedOrderUpdate orderStatus);
}
