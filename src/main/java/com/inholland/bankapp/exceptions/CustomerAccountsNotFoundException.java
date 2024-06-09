package com.inholland.bankapp.exceptions;

public class CustomerAccountsNotFoundException extends RuntimeException {
    public CustomerAccountsNotFoundException() {
        super("Customer accounts not found!");
    }

    public CustomerAccountsNotFoundException(Integer customerId) {
        super("Customer ["+ customerId +"] accounts not found!");
    }
}
