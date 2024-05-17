package com.inholland.bankapp.repository;
import com.inholland.bankapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.inholland.bankapp.model.Bank;

public interface BankRepository extends JpaRepository<Bank, Integer> {
}
