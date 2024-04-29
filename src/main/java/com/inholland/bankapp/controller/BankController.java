package com.inholland.bankapp.controller;

import com.inholland.bankapp.model.User;
import com.inholland.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.inholland.bankapp.model.Bank;
import com.inholland.bankapp.service.BankService;

import java.util.List;
@RestController
@RequestMapping("banks")
public class BankController {

    @Autowired
    private BankService bankService;

    @GetMapping
    public ResponseEntity<List<Bank>> getAllBanks() {
        List<Bank> banks = bankService.getAllBanks();
        if (banks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(banks);
    }


}
