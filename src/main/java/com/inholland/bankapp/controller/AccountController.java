package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.AccountDto;
import com.inholland.bankapp.exceptions.CustomerAccountsNotFoundException;
import com.inholland.bankapp.exceptions.CustomerNotFoundException;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/accounts")
public class AccountController {

    // <editor-fold desc="Initialization components">
    @Autowired
    private AccountService accountService;
    // </editor-fold>

    // <editor-fold desc="Put Endpoints">
    @PutMapping("/{accountIBAN}")
    public ResponseEntity<?> updateAccount(@PathVariable String accountIBAN, @RequestBody AccountDto updatedAccount) {
        try {
            AccountDto account = accountService.updateAccount(accountIBAN, updatedAccount);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // </editor-fold>

    // <editor-fold desc="Get Endpoints">
    @GetMapping("/checking/{IBAN}")
    public ResponseEntity<?> getCheckingAccountByIBAN(@PathVariable String IBAN) {
        try {
            AccountDto account = accountService.getCheckingAccountByIBAN(IBAN);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDto>> getAccountsByCustomerId(@PathVariable Integer customerId) {
        try {
            List<AccountDto> accounts = accountService.getAccountsByCustomerId(customerId);
            return ResponseEntity.ok(accounts);
        } catch (CustomerNotFoundException customerNFEx) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (CustomerAccountsNotFoundException customerAccountsNFEx) {
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
    // </editor-fold>
}
