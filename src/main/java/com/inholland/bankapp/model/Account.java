package com.inholland.bankapp.model;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Account {
    private int account_id;
    private int customer_id;
    private String IBAN;
    private AccountType account_type;
    private float balance;
    private float absolute_transfer_limit;
    private float daily_transfer_limit;

    public Account(int account_id, int customer_id, String IBAN, AccountType account_type, float balance, float absolute_transfer_limit, float daily_transfer_limit) {
        this.account_id = account_id;
        this.customer_id = customer_id;
        this.IBAN = IBAN;
        this.account_type = account_type;
        this.balance = balance;
        this.absolute_transfer_limit = absolute_transfer_limit;
        this.daily_transfer_limit = daily_transfer_limit;
    }
}