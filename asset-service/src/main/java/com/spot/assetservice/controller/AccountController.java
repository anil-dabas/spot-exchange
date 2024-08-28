package com.spot.assetservice.controller;

import com.spot.assetservice.exception.InsufficientBalanceException;
import com.spot.assetservice.model.AccountBalance;
import com.spot.assetservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/balanceDetails")
    public List<AccountBalance> getBalancesDetails(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam(value = "ccy", required = false) String ccy) {
        String userId = userDetails.getUsername();
        if (ccy != null && !ccy.isEmpty()) {
            List<String> currencies = Arrays.asList(ccy.split(","));
            return accountService.getBalancesForCurrencies(userId, currencies);
        } else {
            return accountService.getBalances(userId);
        }
    }

    @GetMapping("/balance")
    public double getBalances(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam(value = "ccy") String ccy) {
        String userId = userDetails.getUsername();

        List<String> currencies = Arrays.asList(ccy.split(","));
        List<AccountBalance> balances = accountService.getBalancesForCurrencies(userId, currencies);

        if(!balances.isEmpty()) {
            return balances.get(0).getBalance();
        }

        return 0.0;
    }

    @PostMapping("/add")
    public boolean addBalance(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam("ccy") String currency,
                              @RequestParam("amount") Double amount) {
        return accountService.addBalance(userDetails.getUsername(), currency, amount);
    }

    @PostMapping("/withdraw")
    public boolean withdrawBalance(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam("ccy") String currency,
                                   @RequestParam("amount") Double amount) {
        return accountService.withdrawBalance(userDetails.getUsername(), currency, amount);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Boolean> transferFunds(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("toUserId") String toUserId,
            @RequestParam("ccy") String currency,
            @RequestParam("amount") Double amount) {

        String fromUserId = userDetails.getUsername();
        try {
            boolean success = accountService.transferBalance(fromUserId, toUserId, currency, amount);
            return ResponseEntity.ok(success);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/freeze")
    public ResponseEntity<Boolean> freezeBalance(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestParam("ccy") String currency,
                                                 @RequestParam("amount") Double amount) {
        try {
            boolean success = accountService.freezeBalance(userDetails.getUsername(), currency, amount);
            return ResponseEntity.ok(success);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/unfreeze")
    public ResponseEntity<Boolean> unfreezeBalance(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam("ccy") String currency,
                                                   @RequestParam("amount") Double amount) {
        try {
            boolean success = accountService.unfreezeBalance(userDetails.getUsername(), currency, amount);
            return ResponseEntity.ok(success);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
