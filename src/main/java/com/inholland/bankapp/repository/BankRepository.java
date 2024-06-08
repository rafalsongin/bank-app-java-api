package com.inholland.bankapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.inholland.bankapp.model.Bank;

public interface BankRepository extends JpaRepository<Bank, Integer> {
}
