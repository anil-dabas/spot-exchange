package com.spot.assetservice.controller;

import com.spot.assetservice.exception.AccountDoesNotExistException;
import com.spot.assetservice.exception.InsufficientBalanceException;
import com.spot.assetservice.model.AccountBalance;
import com.spot.assetservice.service.AccountService;
import com.spot.auth.security.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/inner/v1/account")
@Slf4j
public class AccountInnerController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/balanceDetails")
    public List<AccountBalance> getBalancesDetails(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam(value = "userId") long userId,
                                                   @RequestParam(value = "ccy", required = false) String ccy) {

        if(userDetails != null && !userDetails.getUsername().equals("system")) {
            return null;
        }

        String username = userDetailsService.loadUserByUserId(userId).getUsername();

        if (ccy != null && !ccy.isEmpty()) {
            List<String> currencies = Arrays.asList(ccy.split(","));
            return accountService.getBalancesForCurrencies(username, currencies);
        } else {
            return accountService.getBalances(username);
        }
    }

    @GetMapping("/balance")
    public double getBalances(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(value = "userId") long userId,
                              @RequestParam(value = "ccy") String ccy) {

        if(userDetails != null && !userDetails.getUsername().equals("system")) {
            return 0.0;
        }

        String username = userDetailsService.loadUserByUserId(userId).getUsername();

        List<String> currencies = Arrays.asList(ccy.split(","));
        List<AccountBalance> balances = accountService.getBalancesForCurrencies(username, currencies);

        if(!balances.isEmpty()) {
            return balances.get(0).getBalance();
        }

        return 0.0;
    }

    @PostMapping("/add")
    public boolean addBalance(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(value = "userId") long userId,
                              @RequestParam("ccy") String currency,
                              @RequestParam("amount") Double amount) {

        if(userDetails != null && !userDetails.getUsername().equals("system")) {
            return false;
        }

        String username = userDetailsService.loadUserByUserId(userId).getUsername();

        return accountService.addBalance(username, currency, amount);
    }

    @PostMapping("/withdraw")
    public boolean withdrawBalance(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(value = "userId") long userId,
                                   @RequestParam("ccy") String currency,
                                   @RequestParam("amount") Double amount) {
        if(userDetails != null && !userDetails.getUsername().equals("system")) {
            return false;
        }

        String username = userDetailsService.loadUserByUserId(userId).getUsername();
        return accountService.withdrawBalance(username, currency, amount);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Boolean> transferFunds(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "fromUserId") long fromUserId,
            @RequestParam("toUserId") long toUserId,
            @RequestParam("ccy") String currency,
            @RequestParam("amount") Double amount) {

        if(userDetails != null && !userDetails.getUsername().equals("system")) {
            log.debug("Transfer from {} to {} failed because userDetails {} not valid", fromUserId, toUserId, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        String fromUser = userDetailsService.loadUserByUserId(fromUserId).getUsername();
        String toUser = userDetailsService.loadUserByUserId(toUserId).getUsername();


        try {
            boolean success = accountService.transferBalance(fromUser, toUser, currency, amount);
            return ResponseEntity.ok(success);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(false);
        } catch (AccountDoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/freeze")
    public ResponseEntity<Boolean> freezeBalance(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestParam(value = "userId") long userId,
                                                 @RequestParam("ccy") String currency,
                                                 @RequestParam("amount") Double amount) {

        if(userDetails != null && !userDetails.getUsername().equals("system")) {
            log.debug("Freeze from {} to {} failed because userName {} not valid for freeze request", userId, currency, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        String username =userDetailsService.loadUserByUserId(userId).getUsername();

        try {
            boolean success = accountService.freezeBalance(username, currency, amount);
            return ResponseEntity.ok(success);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/unfreeze")
    public ResponseEntity<Boolean> unfreezeBalance(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam(value = "userId") long userId,
                                                   @RequestParam("ccy") String currency,
                                                   @RequestParam("amount") Double amount) {

        if(userDetails != null && !userDetails.getUsername().equals("system")) {
            log.debug("unFreeze from {} to {} failed because userName {} not valid for unFreeze request", userId, currency, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        String username = userDetailsService.loadUserByUserId(userId).getUsername();

        try {
            boolean success = accountService.unfreezeBalance(username, currency, amount);
            return ResponseEntity.ok(success);
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
