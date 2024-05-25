package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.exceptions.InvalidDataException;
import com.inholland.bankapp.exceptions.UserAlreadyExistsException;
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
public class CustomerService extends UserService {
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
    public Optional<Customer> getCustomerById(Integer id) {
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

    public void registerNewCustomer(CustomerRegistrationDto registrationDto) {
        validateRegistrationData(registrationDto);
        
        if (userExists(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Customer with " + registrationDto.getEmail() + " email already exists.");
        }
        
        Customer user = createCustomer(registrationDto);

        customerRepository.save(user);
    }
    
    private Customer createCustomer(CustomerRegistrationDto registrationDto) {
        Customer user = new Customer();
        String username = registrationDto.getFirstName() + registrationDto.getLastName();
        
        user.setUsername(username);
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setBankId(1);
        user.setUserRole(UserRole.CUSTOMER);
        user.setBSN(registrationDto.getBsn());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setTransactionLimit(0.0f); // Set a default or specified transaction limit
        user.setAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);
        
        return user;
    }
    
    protected void validateRegistrationData(CustomerRegistrationDto registrationDto) {
        if (registrationDto.getEmail() == null || !registrationDto.getEmail().matches("[^@ ]+@[^@ ]+\\.[^@ ]+")) {
            System.out.println("1");
            throw new InvalidDataException("Invalid email format");
        }
        if (registrationDto.getPassword() == null || registrationDto.getPassword().length() < 6) {
            System.out.println("2");
            throw new InvalidDataException("Password must be at least 6 characters long");
        }
        if (registrationDto.getFirstName() == null || registrationDto.getFirstName().isEmpty()) {
            System.out.println("3");
            throw new InvalidDataException("First name is required");
        }
        if (registrationDto.getLastName() == null || registrationDto.getLastName().isEmpty()) {
            System.out.println("4");
            throw new InvalidDataException("Last name is required");
        }
        if (registrationDto.getPhoneNumber() == null || !isValidPhoneNumber(registrationDto.getPhoneNumber())) {
            System.out.println("5");
            throw new InvalidDataException("Invalid Dutch phone number");
        }
        if (registrationDto.getBsn() == null || !isValidBSN(registrationDto.getBsn())) {
            System.out.println("6");
            throw new InvalidDataException("Invalid BSN number");
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("(\\+31|0)[1-9][0-9]{8}");
    }

    private boolean isValidBSN(String bsn) {
        return bsn.length() >= 8 && bsn.length() <= 9 && bsn.matches("\\d+");
    }
}
