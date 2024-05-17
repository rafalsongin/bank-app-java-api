package com.inholland.bankapp.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "customer_id")
    private int customerId;

    private String IBAN;

    @Column(name = "account_type")
    private AccountType accountType;

    private float balance;

    @Column (name = "absolute_transfer_limit")
    private float absoluteTransferLimit;

    @Column (name = "daily_transfer_limit")
    private float dailyTransferLimit;

    @Column (name = "account_status")
    private AccountStatus accountStatus;
}