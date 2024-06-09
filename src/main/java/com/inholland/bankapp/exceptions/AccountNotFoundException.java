package com.inholland.bankapp.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Account not found!");
    }

    public AccountNotFoundException(String iban) {
        super("Account ["+ iban +"] not found!");
    }
}
