package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.CustomerDto;
import com.inholland.bankapp.exceptions.CustomerNotFoundException;
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

    // <editor-fold desc="Get customer methods">

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        try {
            List<CustomerDto> customers = customerService.getAllCustomers();
            if (customers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerDto> getCustomerByEmail(@PathVariable String email) {
        try {
            Optional<CustomerDto> optCustomerDto = customerService.getCustomerDtoByEmail(email);
            return ResponseEntity.ok(optCustomerDto.get());
        } catch (CustomerNotFoundException customerNFEx) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer id) {
        try {
            Optional<CustomerDto> optCustomerDto = customerService.getCustomerDtoById(id);
            return ResponseEntity.ok(optCustomerDto.get());
        } catch (CustomerNotFoundException customerNFEx) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // this one gets only IBAN of account not the whole account
    @GetMapping("/iban/{firstName}/{lastName}")
    public ResponseEntity<String> getIbanByCustomerName(@PathVariable String firstName, @PathVariable String lastName) {
        try {
            String iban = customerService.getIbanByCustomerName(firstName, lastName);
            if (iban == null) {
                return ResponseEntity.noContent().build();
            }
            if (iban.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(iban);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // </editor-fold>

    // <editor-fold desc="Change status of customer account">

    @PostMapping("/approve/{customerID}")
    public ResponseEntity<String> approveCustomer(@PathVariable int customerID) {
        try {
            customerService.approveCustomer(customerID);
            return ResponseEntity.ok("Customer approved");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/decline/{customerID}")
    public ResponseEntity<String> declineCustomer(@PathVariable int customerID) {
        try {
            customerService.declineCustomer(customerID);
            return ResponseEntity.ok("Customer declined");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/close/{customerID}")
    public ResponseEntity<String> closeCustomerAccount(@PathVariable int customerID) {
        try {
            customerService.closeCustomerAccount(customerID);
            return ResponseEntity.ok("Customer account closed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // </editor-fold>


    @PutMapping
    public ResponseEntity<CustomerDto> updateCustomerDetails(@RequestBody CustomerDto customerDto) {
        Optional<CustomerDto> optCustomerDto = customerService.updateCustomerDetails(customerDto);

        if (optCustomerDto.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(optCustomerDto.get());
    }
}