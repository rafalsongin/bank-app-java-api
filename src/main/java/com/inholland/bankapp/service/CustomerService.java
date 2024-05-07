package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.model.AccountApprovalStatus;
import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.model.UserRole;
import com.inholland.bankapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
  
    /**
     Get Method - getting the customer by id
     @param id  - parameter is of Long type, that represents the id for the customer
     @return    - returns the customer, if id parameter is provided.
     */
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public List<Customer> getCustomersWithUnverifiedAccounts() {
        return customerRepository.findByAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);
    }

    public void approveCustomer(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            customer.setAccountApprovalStatus(AccountApprovalStatus.VERIFIED);
            customerRepository.save(customer);
            accountService.createAccounts(customer.getUserId());
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
    
    /*public Customer registerNewCustomerAccount(UserRegistrationDto registrationDto) {
        Customer user = new Customer();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setUsername(user.getFirstName() + user.getLastName());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setUserRole(UserRole.CUSTOMER);
        user.setAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);

        *//*Customer customer = new Customer();
        customer.setEmail("rafal.songin@gmail.com");
        customer.setPassword(passwordEncoder.encode("rafalsongin"));
        customer.setFirstName("Rafal");
        customer.setLastName("Songin");
        customer.setUserRole(UserRole.CUSTOMER);
        customer.setAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);*//*
        return customerRepository.save(user);
    }*/

    public Customer registerNewCustomer(CustomerRegistrationDto registrationDto) {
        Customer user = new Customer();

        // Set username to a combination of firstName and lastName. Ensure this is unique or handled appropriately.
        String username = registrationDto.getFirstName() + registrationDto.getLastName();

        user.setUsername(username);
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());

        // Assuming you are setting some default or fetching an existing bank_id
        user.setBankId(1); // You need to have a valid bank_id or retrieve it dynamically as per your logic

        // Set user role, assuming UserRole.CUSTOMER maps correctly to your user_role in the database
        user.setUserRole(UserRole.CUSTOMER);

        // For Customer specific fields
        user.setBSN(registrationDto.getBsn()); // Assuming you added BSN to your DTO
        user.setPhoneNumber(registrationDto.getPhoneNumber()); // Assuming you added phoneNumber to your DTO
        user.setTransactionLimit(0.0f); // Set a default or specified transaction limit
        user.setAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);

        return customerRepository.save(user);
    }
}
