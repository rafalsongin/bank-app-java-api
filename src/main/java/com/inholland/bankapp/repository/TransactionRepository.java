package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findAll(Pageable pageable);
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :accountId OR t.toAccount = :accountId ORDER BY t.timestamp")
    List<Transaction> findTransactionsByAccountId(@Param("accountId") Integer accountId);

    /**
     Get Method - fetches the daily total transaction amount by accountId
     @param accountId  - parameter is an Integer type, that represents an Id of an account
     @return    - returns the daily total transaction amount of an account, if the value is null returns 0
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount = :accountId AND DATE(timestamp) = CURRENT_DATE")
    Float getAccountTotalDailyTransferAmount(@Param("accountId") Integer accountId);
}

