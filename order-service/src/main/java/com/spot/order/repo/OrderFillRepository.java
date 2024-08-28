package com.spot.order.repo;

import com.spot.order.model.domain.OrderFill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderFillRepository extends JpaRepository<OrderFill, Long> {
}
