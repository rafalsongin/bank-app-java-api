package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.AccountDto;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PutMapping("/{accountIBAN}")
    public ResponseEntity<?> updateAccount(@PathVariable String accountIBAN, @RequestBody AccountDto updatedAccount) {
        try{
            AccountDto account = accountService.updateAccount(accountIBAN, updatedAccount);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/checking/{IBAN}")
    public ResponseEntity<?> getCheckingAccountByIBAN(@PathVariable String IBAN) {
        try {
            AccountDto account = accountService.getCheckingAccountByIBAN(IBAN);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
