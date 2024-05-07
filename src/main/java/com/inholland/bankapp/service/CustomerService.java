package com.inholland.bankapp.service;

import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    /**
     Get Method - gets all customers
     @return    - returns all existing customers
     */
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    /**
     Get Method - getting the customer by id
     @param id  - parameter is of Long type, that represents the id for the customer
     @return    - returns the customer, if id parameter is provided.
     */
    public Optional<Customer> getCustomerById(Long id) {
        return repository.findById(id);
    }
}
