package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.AtmTransactionDto;
import com.inholland.bankapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atm")
public class AtmController {
    
    @Autowired
    private AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<?> getCheckingAccountBalance() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            double balance = accountService.findCheckingAccountBalanceByEmail(email);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred fetching balance");
        }
    }
    
    @PostMapping("/deposit")
    public ResponseEntity<?> depositToCheckingAccount(@RequestBody AtmTransactionDto atmTransactionDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            accountService.depositToCheckingAccount(email, atmTransactionDto.getAmount());
            
            return ResponseEntity.ok("Deposit was successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred fetching balance");
        }
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawFromCheckingAccount(@RequestBody AtmTransactionDto atmTransactionDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            accountService.withdrawFromCheckingAccount(email, atmTransactionDto.getAmount());
            
            return ResponseEntity.ok("Withdraw was successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred fetching balance");
        }
    }
    
}
