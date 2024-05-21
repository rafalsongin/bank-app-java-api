package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :accountId OR t.toAccount = :accountId ORDER BY t.timestamp")
    List<Transaction> findTransactionsByAccountId(@Param("accountId") Integer accountId);
}