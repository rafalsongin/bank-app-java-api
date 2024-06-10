package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.AtmTransactionDto;
import com.inholland.bankapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/atm")
public class AtmController {

    @Autowired
    private AccountService accountService;

    private static final Logger logger = Logger.getLogger(AtmController.class.getName());

    @GetMapping("/balance")
    public ResponseEntity<?> getCheckingAccountBalance() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            logger.info("Email from token: " + email);

            Double balance = accountService.findCheckingAccountBalanceByEmail(email);
            logger.info("Balance retrieved: " + balance);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            logger.severe("Error during balance retrieval: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred fetching balance");
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositToCheckingAccount(@RequestBody AtmTransactionDto atmTransactionDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            logger.info("Email from token: " + email);

            accountService.depositToCheckingAccount(email, atmTransactionDto.getAmount());

            return ResponseEntity.ok("Deposit was successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.severe("Error during deposit: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred fetching balance");
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawFromCheckingAccount(@RequestBody AtmTransactionDto atmTransactionDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            logger.info("Email from token: " + email);

            accountService.withdrawFromCheckingAccount(email, atmTransactionDto.getAmount());

            return ResponseEntity.ok("Withdraw was successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.severe("Error during withdrawal: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred fetching balance");
        }
    }
}
