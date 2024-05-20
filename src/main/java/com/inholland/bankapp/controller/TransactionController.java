package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.TransactionCreationDto;
import com.inholland.bankapp.exceptions.InvalidDataException;
import com.inholland.bankapp.exceptions.UserAlreadyExistsException;
import com.inholland.bankapp.model.Transaction;
import com.inholland.bankapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transactions")
@CrossOrigin(origins = "http://localhost:5173") // this will need changes depending on the port number
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = service.getAllTransactions();

        // Check if the list is empty (not found)
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(transactions);
    }

    /**
     Create Method - creating a transaction
     @param transactionCreationDto  - parameter is an TransactionCreationDto type, that represents a transaction as DTO (Data Transfer Object)
     */
    @PostMapping
    public ResponseEntity<TransactionCreationDto> createTransaction(@RequestBody TransactionCreationDto transactionCreationDto) {
        TransactionCreationDto createdTransaction = service.saveTransaction(transactionCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }
}
