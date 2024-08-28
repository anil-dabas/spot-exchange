package com.spot.order.controller;

import com.spot.auth.model.CustomUserDetails;
import com.spot.order.model.request.AmendOrderParam;
import com.spot.order.model.request.CancelOrderParam;
import com.spot.order.model.request.PlaceOrderParam;
import com.spot.order.model.request.QueryOrderParam;
import com.spot.order.model.response.OrderVo;
import com.spot.order.model.response.ResultVO;
import com.spot.order.service.OrderService;
import com.spot.order.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/trade")
@Validated
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/order")
    ResultVO<OrderVo> placeOrder(@Validated @RequestBody PlaceOrderParam placeOrderParam,@AuthenticationPrincipal CustomUserDetails userDetails){
        ResultVO<OrderVo> resultVo = tradeService.placeOrder(placeOrderParam,userDetails);
        return resultVo;
    }

    @PostMapping("/amend-order")
    ResultVO<OrderVo> amendOrder(@Validated @RequestBody AmendOrderParam amendOrderParam,@AuthenticationPrincipal CustomUserDetails userDetails){
        ResultVO<OrderVo> resultVo = tradeService.amendOrder(amendOrderParam,userDetails);
        return resultVo;
    }

    @PostMapping("/cancel-order")
    ResultVO<OrderVo> cancelOrder(@Validated @RequestBody CancelOrderParam cancelOrderParam,@AuthenticationPrincipal CustomUserDetails userDetails){
        ResultVO<OrderVo> resultVo = tradeService.cancelOrder(cancelOrderParam, userDetails);
        return resultVo;
    }

    @GetMapping("/order")
    ResultVO<OrderVo> queryOneOrder(@Validated @RequestBody Long orderId,@AuthenticationPrincipal CustomUserDetails userDetails){
        ResultVO<OrderVo> resultVo = orderService.queryOrder(orderId, userDetails.getUserId());
        return resultVo;
    }

    @GetMapping("/orders-pending")
    ResultVO<OrderVo> queryPendingOrders(@Validated @RequestBody QueryOrderParam queryOrderParam,@AuthenticationPrincipal CustomUserDetails userDetails){
        ResultVO<OrderVo> resultVo = orderService.queryUnfinishedOrders(queryOrderParam, userDetails.getUserId());
        return resultVo;
    }

    @GetMapping("/orders-history")
    ResultVO<OrderVo> queryHistoryOrders(@Validated @RequestBody QueryOrderParam queryOrderParam,@AuthenticationPrincipal CustomUserDetails userDetails){
        ResultVO<OrderVo> resultVo = orderService.queryHistoryOrder(queryOrderParam, userDetails.getUserId());
        return resultVo;
    }
}
