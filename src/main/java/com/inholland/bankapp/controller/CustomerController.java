package com.inholland.bankapp.controller;

import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("api/customers")
@CrossOrigin(origins = "http://localhost:5173") // this will need changes depending on the port number
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
  
    @GetMapping("/{id}")
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

}
