package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findAll(Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (:minAmount IS NULL OR t.amount >= :minAmount) AND (:maxAmount IS NULL OR t.amount <= :maxAmount)")
    Page<Transaction> findByAmountRange(@Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :accountId OR t.toAccount = :accountId ORDER BY t.timestamp")
    List<Transaction> findTransactionsByAccountId(@Param("accountId") Integer accountId);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:startDate IS NULL OR DATE(t.timestamp) >= :startDate) AND " +
            "(:endDate IS NULL OR DATE(t.timestamp) <= :endDate) AND " +
            "(:amountValue IS NULL OR " +
            "(CASE WHEN :amountCondition = 'equal' THEN t.amount = :amountValue " +
            "WHEN :amountCondition = 'greaterThan' THEN t.amount > :amountValue " +
            "WHEN :amountCondition = 'lessThan' THEN t.amount < :amountValue END)) AND " +
            "(:fromAccountId IS NULL OR t.fromAccount = :fromAccountId) AND " +
            "(:toAccountId IS NULL OR t.toAccount = :toAccountId)")
    Page<Transaction> findByFilters(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("amountCondition") String amountCondition,
            @Param("amountValue") Float amountValue,
            @Param("fromAccountId") Integer fromAccountId,
            @Param("toAccountId") Integer toAccountId,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromAccount = :accountId OR t.toAccount = :accountId) " +
            "AND (:startDate IS NULL OR DATE(t.timestamp) >= :startDate) " +
            "AND (:endDate IS NULL OR DATE(t.timestamp) <= :endDate) " +
            "AND (:amountValue IS NULL OR " +
            "(CASE WHEN :amountCondition = 'equal' THEN t.amount = :amountValue " +
            "WHEN :amountCondition = 'greaterThan' THEN t.amount > :amountValue " +
            "WHEN :amountCondition = 'lessThan' THEN t.amount < :amountValue END)) " +
            "AND (:fromAccountId IS NULL OR t.fromAccount = :fromAccountId) " +
            "AND (:toAccountId IS NULL OR t.toAccount = :toAccountId) " +
            "ORDER BY t.timestamp")
    List<Transaction> findFilteredTransactionsByAccountId(
            @Param("accountId") Integer accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("amountCondition") String amountCondition,
            @Param("amountValue") Float amountValue,
            @Param("fromAccountId") Integer fromAccountId,
            @Param("toAccountId") Integer toAccountId);

}