package com.inholland.bankapp.service;

import com.inholland.bankapp.model.Transaction;
import com.inholland.bankapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    /**
     Get Method - gets all transactions
     @return    - returns all existing transactions
     */
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    /**
     Get Method - getting the transaction by id
     @param id  - parameter is an Integer type, that represents the id of the transaction
     @return    - returns the transaction, if id parameter is provided.
     */
    public Optional<Transaction> getTransactionById(Integer id) {
        return repository.findById(id);
    }
}
