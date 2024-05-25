package com.inholland.bankapp.controller;

import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/accounts")
@CrossOrigin(origins = "http://localhost:5173") // this will need changes depending on the port number
public class AccountController {

    @Autowired
    private AccountService service;

    @GetMapping("/customer/{customerId}")
    public ResponseEntity <List<Account>> getAccountsByCustomerId(@PathVariable Integer customerId){
        List <Account> accounts = service.getAccountsByCustomerId(customerId);

        if (accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/iban/{accountIban}")
    public ResponseEntity<Account> getAccountByIBAN(@PathVariable String accountIban){
        Optional<Account> account = service.getAccountByIBAN(accountIban);

        if (account.isPresent()) {
            return ResponseEntity.ok(account.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
