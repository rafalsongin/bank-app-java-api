package com.inholland.bankapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.inholland.bankapp.model.Bank;
import com.inholland.bankapp.service.BankService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("banks")
@CrossOrigin(origins = "http://localhost:5173") // this will need changes depending on the port number
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
