package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.Employee;
import com.inholland.bankapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<User> findByEmail(String email);
}
