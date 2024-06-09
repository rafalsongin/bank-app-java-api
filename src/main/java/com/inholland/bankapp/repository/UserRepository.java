package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a WHERE a.IBAN = :iban AND a.customerId = :customerId")
    boolean isAccountOwner(@Param("iban") String iban, @Param("customerId") int customerId);
}