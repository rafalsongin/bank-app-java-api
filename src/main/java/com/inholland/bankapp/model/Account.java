package com.inholland.bankapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import java.time.*;

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

    @OneToMany(mappedBy = "fromAccountEntity")
    @JsonIgnore
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toAccountEntity")
    @JsonIgnore
    private List<Transaction> incomingTransactions;

    @ManyToOne
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private User customer;

//    @OneToMany(mappedBy = "from_account")
//    private List<Transaction> outgoingTransactions;

    // Getter for customer's full name
    public String getCustomerFullName() {
        return customer.getFirstName() + " " + customer.getLastName();
    }

    // Getter for available daily amount for transfer
    public float getAvailableDailyAmountForTransfer() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Amsterdam"));
        float totalTransfersToday = outgoingTransactions.stream()
                .filter(t -> t.getTimestamp().toLocalDate().isEqual(today))
                .map(Transaction::getAmount)
                .reduce(0.0f, Float::sum);
        return dailyTransferLimit - totalTransfersToday;
    }

    // Getter for available absolute amount for transfer
    public float getAvailableAbsoluteAmountForTransfer() {
        return balance - absoluteTransferLimit;
    }
}