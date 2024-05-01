package com.inholland.bankapp.service;

import com.inholland.bankapp.model.AccountApprovalStatus;
import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    public List<Customer> getCustomersWithUnverifiedAccounts() {
        return customerRepository.findByApprovalStatus(AccountApprovalStatus.UNVERIFIED);
    }
}