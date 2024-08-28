package com.spot.marketdata.repository;

import com.spot.marketdata.model.OHLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OHLCRepository extends JpaRepository<OHLC, Long> {
    Optional<OHLC> findTopBySymbolOrderByIdDesc(String symbol);

    Optional<OHLC> findTopBySymbolAndCloseTimeLessThanOrderByCloseTimeDesc(String symbol, long endOfYesterdayMillis);


    @Query("SELECT o FROM OHLC o WHERE o.symbol = :symbol AND o.interval = :interval ORDER BY o.closeTime DESC")
    Optional<OHLC> findLatestBySymbolAndInterval(String symbol, String interval);

}