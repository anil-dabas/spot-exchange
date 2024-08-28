package com.spot.order.service;

import com.spot.auth.model.CustomUserDetails;
import com.spot.order.model.request.AmendOrderParam;
import com.spot.order.model.request.CancelOrderParam;
import com.spot.order.model.request.PlaceOrderParam;
import com.spot.order.model.response.OrderVo;
import com.spot.order.model.response.ResultVO;

public interface TradeService {
    ResultVO<OrderVo> placeOrder(PlaceOrderParam placeOrderParam, CustomUserDetails userDetails);

    ResultVO<OrderVo> amendOrder(AmendOrderParam amendOrderParam, CustomUserDetails userDetails);
    
    ResultVO<OrderVo> cancelOrder(CancelOrderParam cancelOrderParam, CustomUserDetails userDetails);

/*    ResultVO<OrderVo> placeBatchOrders(List<PlaceOrderParam> placeOrderParams);

    ResultVO<OrderVo> cancelBatchOrder();

    ResultVO<OrderVo> amendBatchOrder(List<AmendOrderParam> amendOrderParams);*/

}
