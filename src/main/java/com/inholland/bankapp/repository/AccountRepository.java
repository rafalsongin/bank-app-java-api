package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByIBAN(String IBAN);
    List<Account> getAccountsByCustomerId(int customer_id);

}