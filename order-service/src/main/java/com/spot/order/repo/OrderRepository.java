package com.spot.order.repo;

import com.spot.order.model.domain.OrderState;
import com.spot.order.model.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllByStateAndUserId(OrderState orderState, Long userId);

    List<Order> findAllByUserId(Long userId);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    Order findByOrderId(long orderId);
}
