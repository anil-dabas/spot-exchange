package com.spot.order.model.request;

import com.spot.order.model.domain.OrderState;
import com.spot.order.model.domain.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryOrderParam {

    private OrderType orderType;
    private String instId;
    private OrderState state;
    private boolean filterCancel;
    private boolean allOrders;
    private List<Long> orderIds;
}
