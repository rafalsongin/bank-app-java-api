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
    Optional<Account> findAccountByIBAN(String IBAN);
    List<Account> findAccountsByCustomerId(int customer_id);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = CASE WHEN a.accountId = :fromAccountId THEN :fromAccountBalance " +
            "WHEN a.accountId = :toAccountId THEN :toAccountBalance END " +
            "WHERE a.accountId IN (:fromAccountId, :toAccountId)")
    void updateAccountBalances(@Param("fromAccountId") Integer fromAccountId,
                               @Param("fromAccountBalance") Float fromAccountBalance,
                               @Param("toAccountId") Integer toAccountId,
                               @Param("toAccountBalance") Float toAccountBalance);
}
