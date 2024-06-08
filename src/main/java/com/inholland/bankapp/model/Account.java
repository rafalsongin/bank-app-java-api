package com.inholland.bankapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


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

    @JsonIgnore
    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> outgoingTransactions = null;

    // Getter for available daily amount for transfer
    public float getAvailableDailyAmountForTransfer() {
        if (outgoingTransactions == null) {
            return dailyTransferLimit;
        }

        LocalDate today = LocalDate.now(ZoneId.of("Europe/Amsterdam"));
        float totalTransfersToday = outgoingTransactions.stream()
                .filter(t -> t.getTimestamp().toLocalDate().isEqual(today))
                .map(Transaction::getAmount)
                .reduce(0.0f, Float::sum);

        return dailyTransferLimit - totalTransfersToday;
    }
}
