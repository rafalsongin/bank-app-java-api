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
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerDto> getCustomerByEmail(@PathVariable String email) {
        System.out.println("Received request for email: " + email);

        Optional<CustomerDto> optCustomerDto = customerService.getCustomerDtoByEmail(email);
        if (!optCustomerDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(optCustomerDto.get());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer id) {
        Optional<CustomerDto> customerDto = customerService.getCustomerDtoById(id);

        // Check if the object was not found
        if (!customerDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(customerDto.get());
    }

    @GetMapping("/unverified")
    public ResponseEntity<List<CustomerDto>> getUnverifiedCustomers() {
        List<CustomerDto> customers = customerService.getCustomersWithUnverifiedAccounts();
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

    @PutMapping
    public ResponseEntity<CustomerDto> updateCustomerDetails(@RequestBody CustomerDto customerDto){
        Optional<CustomerDto> optCustomerDto = customerService.updateCustomerDetails(customerDto);

        if(optCustomerDto.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(optCustomerDto.get());
    }
}