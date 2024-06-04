package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.model.Transaction;
import com.inholland.bankapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getAllTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionDto> transactions = service.getAllTransactions(page, size);

        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transactions);
    }

    /**
     Create Method - creating a transaction
     @param iban  - parameter is a String type, that represents iban received through path.
     */
    @GetMapping("/account/{iban}")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByIban(@PathVariable String iban) {
        List<TransactionDto> transactions = service.getAllTransactionsByIban(iban);

        // Check if the list is empty (not found)
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transactions);
    }

    /**
     Create Method - creating a transaction
     @param transactionDto  - parameter is an TransactionCreationDto type, that represents a transaction as DTO (Data Transfer Object)
     */
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        TransactionDto createdTransaction = service.saveTransaction(transactionDto);

        if(createdTransaction == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @GetMapping("/{customerID}")
    public ResponseEntity<List<TransactionDto>> getCustomerTransactions(@PathVariable int customerID) {
        List<TransactionDto> transactions = service.getCustomerTransactions(customerID);
        return ResponseEntity.ok(transactions);
    }
}