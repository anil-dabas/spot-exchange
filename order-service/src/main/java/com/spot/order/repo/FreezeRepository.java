package com.spot.order.repo;

import com.spot.order.model.domain.FreezeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreezeRepository extends JpaRepository<FreezeRecord, Long> {
    FreezeRecord findByRequestIdAndUserIdAndCurrencySymbolAndValidFreeze(Long requestId, Long userId, String quoteCurrency, boolean validFreeze);

}