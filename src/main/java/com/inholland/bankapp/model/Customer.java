package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Customer extends User {
    private int customer_id;
    private String BSNNumber;
    private String phoneNumber;
    private String accountApprovalStatus;
    private float dailyTransactionLimit;

    public Customer(int user_id, String username, String email, String password, boolean isEmployee, String JWT, String first_name, String last_name,
                    String BSNNumber, String phoneNumber, String accountApprovalStatus, float dailyTransactionLimit, int bank_id) {
        super(user_id, username, email, password, isEmployee, JWT, first_name, last_name,bank_id);
        this.BSNNumber = BSNNumber;
        this.phoneNumber = phoneNumber;
        this.accountApprovalStatus = accountApprovalStatus;
        this.dailyTransactionLimit = dailyTransactionLimit;
    }
}

