package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getAllTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String amountCondition,
            @RequestParam(required = false) Float amountValue,
            @RequestParam(required = false) String fromIban,
            @RequestParam(required = false) String toIban) {

        Page<TransactionDto> transactions = service.getAllTransactions(page, size, startDate, endDate, amountCondition, amountValue, fromIban, toIban);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{iban}")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByIban(
            @PathVariable String iban,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String amountCondition,
            @RequestParam(required = false) Float amountValue,
            @RequestParam(required = false) String fromIban,
            @RequestParam(required = false) String toIban) {

        List<TransactionDto> transactions = service.getAllTransactionsByIban(iban, startDate, endDate, amountCondition, amountValue, fromIban, toIban);

        return ResponseEntity.ok(transactions);
    }

    // for customer panel temporary
    @GetMapping("/accountId/{accountId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByAccountId(
            @PathVariable Integer accountId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String amountCondition,
            @RequestParam(required = false) Float amountValue,
            @RequestParam(required = false) String fromIban,
            @RequestParam(required = false) String toIban) {

        List<TransactionDto> transactions = service.getAllTransactionsByAccountId(accountId, startDate, endDate, amountCondition, amountValue, fromIban, toIban);

        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(transactions);
    }

    /**
     Create Method - creating a transaction
     @param transactionDto  - parameter is an TransactionCreationDto type, that represents a transaction as DTO (Data Transfer Object)
     */
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDto transactionDto) {
        try {
            TransactionDto createdTransaction = service.saveTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}