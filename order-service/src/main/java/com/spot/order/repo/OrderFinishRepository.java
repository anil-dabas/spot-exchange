package com.spot.order.repo;

import com.spot.order.model.domain.OrderFinish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderFinishRepository extends JpaRepository<OrderFinish, Long> {
}
