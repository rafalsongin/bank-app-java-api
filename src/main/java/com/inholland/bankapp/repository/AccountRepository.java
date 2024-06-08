package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByIBAN(String IBAN);
    List<Account> getAccountsByCustomerId(int customerId);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = CASE WHEN a.accountId = :fromAccountId THEN :fromAccountBalance " +
            "WHEN a.accountId = :toAccountId THEN :toAccountBalance END " +
            "WHERE a.accountId IN (:fromAccountId, :toAccountId)")
    void updateAccountBalances(@Param("fromAccountId") Integer fromAccountId,
                               @Param("fromAccountBalance") Float fromAccountBalance,
                               @Param("toAccountId") Integer toAccountId,
                               @Param("toAccountBalance") Float toAccountBalance);

    @Query("SELECT a.balance FROM Account a JOIN Customer c ON a.customerId = c.userId JOIN User u ON c.userId = u.userId WHERE u.email = :email AND a.accountType = 0")
    double findCheckingAccountBalanceByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.accountType = 0 AND a.customerId = (SELECT u.userId FROM User u WHERE u.email = :email)")
    void depositToCheckingAccount(@Param("email") String email, @Param("amount") double amount);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = a.balance - :amount WHERE a.accountType = 0 AND a.customerId = (SELECT u.userId FROM User u WHERE u.email = :email)")
    void withdrawFromCheckingAccount(@Param("email") String email, @Param("amount") double amount);
    
    @Query("SELECT a.accountId FROM Account a JOIN Customer c ON a.customerId = c.userId JOIN User u ON c.userId = u.userId WHERE u.email = :email AND a.accountType = 0")
    int getCheckingAccountIdByEmail(@Param("email") String email);

    @Query("SELECT u.userId FROM User u JOIN Account a ON a.customerId = u.userId WHERE a.accountId = :accountId")
    int getUserIdByAccountId(@Param("accountId") int accountId);
}
