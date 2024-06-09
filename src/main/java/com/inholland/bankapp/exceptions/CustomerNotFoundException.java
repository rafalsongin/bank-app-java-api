package com.inholland.bankapp.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException() {
        super("Customer not found!");
    }

    public CustomerNotFoundException(Integer customerId) {
        super("Customer [" + customerId + "] not found!");
    }

    public CustomerNotFoundException(String email) {
        super("Customer [" + email + "] not found!");
    }
}
