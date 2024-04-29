package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Customer extends User {
    private String BSNNumber;
    private String phoneNumber;
    private String accountApprovalStatus;
    private String IBAN;
    private float accountBalance;
    private float dailyTransactionLimit;

    public Customer(int user_id, String username, String email, String password, String role, String JWT, String first_name, String last_name,
                    String BSNNumber, String phoneNumber, String accountApprovalStatus, String IBAN, float accountBalance, float dailyTransactionLimit) {
        super(user_id, username, email, password, role, JWT, first_name, last_name);
        this.BSNNumber = BSNNumber;
        this.phoneNumber = phoneNumber;
        this.accountApprovalStatus = accountApprovalStatus;
        this.IBAN = IBAN;
        this.accountBalance = accountBalance;
        this.dailyTransactionLimit = dailyTransactionLimit;
    }
}

