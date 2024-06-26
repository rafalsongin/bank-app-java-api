package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.exceptions.AccountNotFoundException;
import com.inholland.bankapp.model.UserRole;
import com.inholland.bankapp.service.TransactionService;
import com.inholland.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String amountCondition,
            @RequestParam(required = false) Float amountValue,
            @RequestParam(required = false) String fromIban,
            @RequestParam(required = false) String toIban,
            @RequestParam String username,
            @RequestParam String role) {
        try {
            if (UserRole.valueOf(role.toUpperCase()) != UserRole.EMPLOYEE || !userService.userExists(username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }
            Page<TransactionDto> transactions = service.getAllTransactions(page, size, startDate, endDate, amountCondition, amountValue, fromIban, toIban);
            return ResponseEntity.ok(transactions);}
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccountNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching transactions. Please try again later.");
        }
    }

    @GetMapping("/account/{iban}")
    public ResponseEntity<?> getAllTransactionsByIban(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String iban,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String amountCondition,
            @RequestParam(required = false) Float amountValue,
            @RequestParam(required = false) String fromIban,
            @RequestParam(required = false) String toIban,
            @RequestParam String username,
            @RequestParam String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            if (userRole == UserRole.EMPLOYEE && !userService.userExists(username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }
            else if (userRole == UserRole.CUSTOMER && !userService.userExists(username) && !userService.isAccountOwner(username, iban)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }
            Page<TransactionDto> transactions = service.getAllTransactionsByIban(page, size, iban, startDate, endDate, amountCondition, amountValue, fromIban, toIban);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccountNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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