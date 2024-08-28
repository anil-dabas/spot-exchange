package com.spot.marketdata.repository;

import com.spot.marketdata.model.MarketDepth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketDepthRepository extends JpaRepository<MarketDepth, Long> {
    Optional<MarketDepth> findTopBySymbolOrderByIdDesc(String symbol);

    Optional<MarketDepth> findBySymbol(String symbol);
}