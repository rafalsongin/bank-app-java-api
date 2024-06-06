package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.CustomerDto;
import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.exceptions.InvalidDataException;
import com.inholland.bankapp.exceptions.UserAlreadyExistsException;
import com.inholland.bankapp.model.*;
import com.inholland.bankapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService extends UserService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BankService bankService;

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


    public void closeCustomerAccount(int customerID) {
        Customer customer = customerRepository.findById(customerID).orElse(null);
        if (customer != null) {
            customer.setAccountApprovalStatus(AccountApprovalStatus.CLOSED);
            customerRepository.save(customer);
        }
        else {
            throw new IllegalArgumentException("Customer not found");
        }
    }

    /**
     Get Method - getting the customer by email
     @param email  - parameter is of String type, that represents the email of the customer
     @return    - returns the customer, if email parameter is provided.
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        Optional<Customer> customer = customerRepository.getCustomerByEmail(email);
        System.out.println("Customer fetched from repository: " + customer.get().getUsername());
        return customer;
    }

    /**
     Check Method - check if customer exists by using the email
     @param email  - parameter is of String type, that represents the email of the customer
     @return    - returns a boolean value
     */
    private boolean checkCustomerExists(String email) {
        return customerRepository.getCustomerByEmail(email).isPresent();
    }

    public String getIbanByCustomerName(String firstName, String lastName) {
        Customer customer = customerRepository.findByFirstNameAndLastName(firstName, lastName);
        String checkingAccountIban = "";
        if (customer != null) {
            List<Account> accounts = accountService.getAccountsByCustomerId(customer.getUserId());
            if (accounts.size() > 0) {
                Account checkingAccount ;
                checkingAccount = accounts.stream().filter(account -> account.getAccountType() == AccountType.CHECKING).max(Comparator.comparing(Account::getAccountId)).get();
                checkingAccountIban = checkingAccount.getIBAN();
            }
        }
        return checkingAccountIban;
    }

    /**
     Update Method - checks if the customerDto has changes from the original customer
     @param customerDto  - parameter is a customerDto class, that represents a customer as DTO (Data Transfer Object)
     @return    - returns a boolean, 'true' if changes were made and 'false' if there are no changes.
     */
    public Optional<CustomerDto> updateCustomerDetails(CustomerDto customerDto) {
        try {
            Customer customer = getCustomerByEmail(customerDto.getEmail()).get();

            if(!isCustomerModified(customerDto, customer)){
                System.out.println("[Warning] Customer details were not modified!");
                return Optional.empty();
            }

            changeCustomerDetailsWithModified(customerDto, customer);
            Customer updatedCustomer = customerRepository.save(customer);

            return Optional.of(transformCustomerIntoDto(updatedCustomer));
        }catch (Exception e){
            System.out.println("[Error] Updating customer: " + e);
            return Optional.empty();
        }
    }

    private void changeCustomerDetailsWithModified(CustomerDto customerDto, Customer customer) {
        customer.setUsername(customerDto.getUsername());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setBSN(customerDto.getBsn());
    }

    /**
     Check Method - checks if the customerDto has changes from the original customer
     @param customerDto  - parameter is a customerDto class, that represents a customer as DTO (Data Transfer Object)
     @param customer    - parameter is a customer class, that represents the customer as an object
     @return    - returns a boolean, 'true' if changes were made and 'false' if there are no changes.
     */
    private boolean isCustomerModified(CustomerDto customerDto, Customer customer) {
        boolean result = false;
        System.out.printf("Checking if customer is modified: ");
        if(!customerDto.getUsername().equals(customer.getUsername())) {
            System.out.printf("Username modified; ");
            result = true;
        }
        if(!customerDto.getFirstName().equals(customer.getFirstName())) {
            System.out.printf("FirstName modified; ");
            result = true;
        }
        if(!customerDto.getLastName().equals(customer.getLastName())) {
            System.out.printf("LastName modified; ");
            result = true;
        }
        if(!customerDto.getPhoneNumber().equals(customer.getPhoneNumber())) {
            System.out.printf("PhoneNumber modified; ");
            result = true;
        }
        if(!customerDto.getBsn().equals(customer.getBSN())) {
            System.out.println("BSN modified; ");
            result = true;
        }

        if(!result) System.out.println("no modifications found!");
        return result;
    }

    /**
     Transform Method - transforms a customer object to a customerDto object
     @param customer  - parameter is a customer class, which is used in the back end for customers
     @return    - returns customerDto object
     */
    private CustomerDto transformCustomerIntoDto(Customer customer){
        CustomerDto customerDto = new CustomerDto();

        Optional<Bank> optBank = bankService.getBankById(customer.getBankId());

        customerDto.setUsername(customer.getUsername());
        customerDto.setEmail(customer.getEmail());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setBankName(optBank.get().getName());
        customerDto.setUserRole(customer.getUserRole());
        customerDto.setBsn(customer.getBSN());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setAccountApprovalStatus(customer.getAccountApprovalStatus());

        return customerDto;
    }
}