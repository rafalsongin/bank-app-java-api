package com.inholland.bankapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private int accountId;

    @NotNull
    @Column(name = "customer_id")
    private int customerId;

    @NotNull
    @Column(name = "IBAN")
    private String IBAN;

    @Column(name = "account_type")
    private AccountType accountType;

    @NotNull
    @Column(name = "balance")
    private float balance;

    @NotNull
    @Column (name = "absolute_transfer_limit")
    private float absoluteTransferLimit;

    @NotNull
    @Column (name = "daily_transfer_limit")
    private float dailyTransferLimit;
}
