package com.inholland.bankapp.service;

import com.inholland.bankapp.model.AccountApprovalStatus;
import com.inholland.bankapp.model.AccountType;
import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inholland.bankapp.repository.AccountRepository;
import com.inholland.bankapp.service.AccountService;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountService accountService;




    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> getCustomersWithUnverifiedAccounts() {
        return customerRepository.findByAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);
    }

    public void approveCustomer(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            customer.setAccountApprovalStatus(AccountApprovalStatus.VERIFIED);
            customerRepository.save(customer);
            accountService.createAccounts(customer.getUser_id());
        }
        else {
            throw new IllegalArgumentException("Customer not found");
        }
    }

    public void declineCustomer(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            customer.setAccountApprovalStatus(AccountApprovalStatus.DECLINED);
            customerRepository.save(customer);
        }
        else {
            throw new IllegalArgumentException("Customer not found");
        }
    }






}