package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Customer {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String BSNNumber;
    private String phoneNumber;
    private String IBAN;
    private float accountBalance;
    private float dailyTransactionLimit;
    
    
    public Customer(String id, String firstName, String lastName, String email, String BSNNumber, String phoneNumber, String IBAN, float accountBalance, float dailyTransactionLimit) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.BSNNumber = BSNNumber;
        this.phoneNumber = phoneNumber;
        this.IBAN = IBAN;
        this.accountBalance = accountBalance;
        this.dailyTransactionLimit = dailyTransactionLimit;
    }
}
