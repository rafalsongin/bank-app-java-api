package com.inholland.bankapp.controller;

import com.inholland.bankapp.model.Transaction;
import com.inholland.bankapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/transactions")
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

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer id){
        Transaction transaction = service.getTransactionById(id).orElse(null);

        // Check if the object was not found
        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(transaction);
    }
}
