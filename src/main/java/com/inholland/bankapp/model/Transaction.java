package com.inholland.bankapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Setter
@Getter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "amount")
    private float amount;

    @Column(name = "timestamp")
    private LocalDateTime timestamp; // changed here to LocalDateTime

    @Column(name = "from_account")
    private int fromAccount;

    @Column(name = "to_account")
    private int toAccount;

    @Column(name = "initiated_by_user")
    private int initiatedByUser;

    @ManyToOne
    @JoinColumn(name = "from_account", insertable = false, updatable = false)
    private Account fromAccountEntity;

    @ManyToOne
    @JoinColumn(name = "to_account", insertable = false, updatable = false)
    private Account toAccountEntity;
}
