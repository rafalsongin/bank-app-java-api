package com.inholland.bankapp.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private int account_id;
    private int customer_id;
    private String IBAN;
    private AccountType account_type;
    private float balance;
    private float absolute_transfer_limit;
    private float daily_transfer_limit;

}