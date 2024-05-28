package com.inholland.bankapp.repository;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmRepository extends JpaRepository<User, Integer> {
    
}
