package com.spot.assetservice.repository;

import com.spot.assetservice.model.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {
    List<AccountBalance> findByUserId(String userId);
    List<AccountBalance> findByUserIdAndCurrencyIn(String userId, List<String> currencies);

    @Transactional
    @Modifying
    @Query("UPDATE AccountBalance ab SET ab.balance = ab.balance + ?1 WHERE ab.userId = ?2 AND ab.currency = ?3")
    int addBalance(Double amount, String userId, String currency);

    @Transactional
    @Modifying
    @Query("UPDATE AccountBalance ab SET ab.balance = ab.balance - ?1 WHERE ab.userId = ?2 AND ab.currency = ?3 AND ab.balance >= ?1")
    int withdrawBalance(Double amount, String userId, String currency);

    @Query("SELECT ab FROM AccountBalance ab WHERE ab.userId = :userId AND ab.currency = :currency")
    Optional<AccountBalance> findByUserIdAndCurrency(String userId, String currency);

    @Transactional
    @Modifying
    @Query("UPDATE AccountBalance ab SET ab.balance = ab.balance - ?1, ab.frozenBalance = ab.frozenBalance + ?1 WHERE ab.userId = ?2 AND ab.currency = ?3 AND ab.balance >= ?1")
    int freezeBalance(Double amount, String userId, String currency);

    @Transactional
    @Modifying
    @Query("UPDATE AccountBalance ab SET ab.balance = ab.balance + ?1, ab.frozenBalance = ab.frozenBalance - ?1 WHERE ab.userId = ?2 AND ab.currency = ?3 AND ab.frozenBalance >= ?1")
    int unfreezeBalance(Double amount, String userId, String currency);
}
