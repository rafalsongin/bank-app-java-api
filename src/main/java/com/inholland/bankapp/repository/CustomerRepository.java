package com.inholland.bankapp.repository;

import com.inholland.bankapp.model.AccountApprovalStatus;
import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> getCustomerByEmail(String email);
    Customer findByFirstNameAndLastName(String first_name, String last_name);

}
