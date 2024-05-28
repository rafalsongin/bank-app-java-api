package com.inholland.bankapp.controller;

import com.inholland.bankapp.service.AccountService;
import com.inholland.bankapp.service.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred fetching balance");
    }
    
}
