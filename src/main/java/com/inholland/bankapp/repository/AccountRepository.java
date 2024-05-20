package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findAccountByIBAN(String IBAN);
    List<Account> findAccountsByCustomerId(int customer_id);
}