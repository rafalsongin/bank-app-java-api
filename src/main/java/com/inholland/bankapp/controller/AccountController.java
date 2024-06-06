package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.AccountDto;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PutMapping("/changeAccount/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable int accountId, @RequestBody Account updatedAccount) {
        Account account = accountService.updateAccount(accountId, updatedAccount);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @GetMapping("/getCheckingAccount/{IBAN}")
    public ResponseEntity<AccountDto> getCheckingAccountByIBAN(@PathVariable String IBAN) {
        AccountDto account = accountService.getCheckingAccountByIBAN(IBAN);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDto>> getAccountsByCustomerId(@PathVariable Integer customerId) {
        List<AccountDto> accounts = accountService.getAccountsByCustomerId(customerId);

        if (accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/iban/{accountIban}")
    public ResponseEntity<Account> getAccountByIBAN(@PathVariable String accountIban) {
        Optional<Account> account = accountService.getAccountByIBAN(accountIban);

        if (account.isPresent()) {
            return ResponseEntity.ok(account.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
