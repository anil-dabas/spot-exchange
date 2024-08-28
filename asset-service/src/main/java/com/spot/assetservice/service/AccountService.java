package com.spot.assetservice.service;

import com.spot.assetservice.exception.AccountDoesNotExistException;
import com.spot.assetservice.exception.InsufficientBalanceException;
import com.spot.assetservice.model.AccountBalance;
import com.spot.assetservice.repository.AccountBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AccountService {

    private final AccountBalanceRepository accountBalanceRepository;

    @Autowired
    public AccountService(AccountBalanceRepository accountBalanceRepository){
        this.accountBalanceRepository = accountBalanceRepository;
    }

    public List<AccountBalance> getBalances(String userId) {
        return accountBalanceRepository.findByUserId(userId);
    }

    public List<AccountBalance> getBalancesForCurrencies(String userId, List<String> currencies) {
        return accountBalanceRepository.findByUserIdAndCurrencyIn(userId, currencies);
    }

    @Transactional
    public boolean addBalance(String userId, String currency, Double amount) {
        List<AccountBalance> existingBalances = accountBalanceRepository.findByUserIdAndCurrencyIn(userId, List.of(currency));

        if (existingBalances.isEmpty()) {
            // If user does not exist, create a new entry
            AccountBalance newBalance = new AccountBalance();
            newBalance.setUserId(userId);
            newBalance.setCurrency(currency);
            newBalance.setBalance(amount);
            newBalance.setFrozenBalance(0.0);
            accountBalanceRepository.save(newBalance);
            return true;
        } else {
            // If user exists, add the amount to the existing balance
            int updated = accountBalanceRepository.addBalance(amount, userId, currency);
            return updated > 0;
        }
    }

    @Transactional
    public boolean withdrawBalance(String userId, String currency, Double amount) {
        // First, check if the balance is sufficient
        List<AccountBalance> balances = accountBalanceRepository.findByUserIdAndCurrencyIn(userId, List.of(currency));
        if (balances.isEmpty() || balances.get(0).getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal.");
        }
        // Perform the withdrawal
        int updated = accountBalanceRepository.withdrawBalance(amount, userId, currency);
        return updated > 0;
    }

    @Transactional
    public boolean transferBalance(String fromUserId, String toUserId, String currency, Double amount) throws AccountDoesNotExistException {
        log.info("Transfer balance from {} to {}, amount : {}, currency : {}", fromUserId, toUserId, amount, currency);
        // Check if the source account has sufficient balance
        AccountBalance sourceAccount = accountBalanceRepository.findByUserIdAndCurrency(fromUserId, currency)
                .orElseThrow(() -> new AccountDoesNotExistException("Source account has insufficient balance."));

        if (sourceAccount.getFrozenBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance for transfer.");
        }

        // Deduct the amount from the source frozen account
        sourceAccount.setFrozenBalance(sourceAccount.getFrozenBalance() - amount);


        // Check if the destination account exists
        AccountBalance destinationAccount = accountBalanceRepository.findByUserIdAndCurrency(toUserId, currency)
                .orElse(new AccountBalance());

        // If the destination account does not exist, initialize it
        if (destinationAccount.getUserId() == null) {
            destinationAccount.setUserId(toUserId);
            destinationAccount.setCurrency(currency);
            destinationAccount.setBalance(0.0);
            destinationAccount.setFrozenBalance(0.0);
        }

        // Add the amount to the destination account
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

        accountBalanceRepository.save(sourceAccount);
        accountBalanceRepository.save(destinationAccount);

        return true;
    }

    @Transactional
    public boolean freezeBalance(String userId, String currency, Double amount) {
        AccountBalance accountBalance = accountBalanceRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new InsufficientBalanceException("Account not found."));

        if (accountBalance.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance to freeze.");
        }

        accountBalance.setBalance(accountBalance.getBalance() - amount);
        accountBalance.setFrozenBalance(accountBalance.getFrozenBalance() + amount);
        accountBalanceRepository.save(accountBalance);
        return true;
    }

    @Transactional
    public boolean unfreezeBalance(String userId, String currency, Double amount) {
        AccountBalance accountBalance = accountBalanceRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new InsufficientBalanceException("Account not found."));

        if (accountBalance.getFrozenBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient frozen balance to unfreeze.");
        }

        accountBalance.setBalance(accountBalance.getBalance() + amount);
        accountBalance.setFrozenBalance(accountBalance.getFrozenBalance() - amount);
        accountBalanceRepository.save(accountBalance);
        return true;
    }
}
