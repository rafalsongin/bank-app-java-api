package com.inholland.bankapp.controller;


import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.service.AccountService;
import com.inholland.bankapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.List;

@RestController
@RequestMapping("api/accounts")
@CrossOrigin(origins = "http://localhost:5173") // this will need changes depending on the port number
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{customerId}")
    public ResponseEntity <List<Account>> getAccountsByCustomerId(@PathVariable int customerId){
        List <Account> accounts = accountService.getAccountsByCustomerId(customerId);
        if (accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(accounts);
    }

}
