package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.CustomerDto;
import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String email) {
        System.out.println("Received request for email: " + email);

        Optional<Customer> customerOpt = customerService.getCustomerByEmail(email);
        if (!customerOpt.isPresent()) {
            System.out.println("Customer not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Customer customer = customerOpt.get();
        System.out.println("Customer found: " + customer.getUsername());
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        Customer customer = customerService.getCustomerById(id).orElse(null);

        // Check if the object was not found
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(customer);
    }

    @GetMapping("/unverified")
    public ResponseEntity<List<Customer>> getUnverifiedCustomers() {
        List<Customer> customers = customerService.getCustomersWithUnverifiedAccounts();
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/approve/{customerID}")
    public ResponseEntity<String> approveCustomer(@PathVariable int customerID) {
        customerService.approveCustomer(customerID);
        return ResponseEntity.ok("Customer approved");
    }

    @PostMapping("/decline/{customerID}")
    public ResponseEntity<String> declineCustomer(@PathVariable int customerID) {
        customerService.declineCustomer(customerID);
        return ResponseEntity.ok("Customer declined");
    }

    @PutMapping("/closeAccount/{customerID}")
    public ResponseEntity<String> closeCustomerAccount(@PathVariable int customerID) {
        customerService.closeCustomerAccount(customerID);
        return ResponseEntity.ok("Customer account closed");
    }

    @GetMapping("/getIbanByCustomerName/{firstName}/{lastName}")
    public ResponseEntity<String> getIbanByCustomerName(@PathVariable String firstName, @PathVariable String lastName) {
        String iban = customerService.getIbanByCustomerName(firstName, lastName);
        if (iban == null) {
            return ResponseEntity.noContent().build();
        }
        if (iban.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(iban);
    }

    @PutMapping
    public ResponseEntity<CustomerDto> updateCustomerDetails(@RequestBody CustomerDto customerDto){
        Optional<CustomerDto> optCustomerDto = customerService.updateCustomerDetails(customerDto);

        if(optCustomerDto.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(optCustomerDto.get());
    }
}



