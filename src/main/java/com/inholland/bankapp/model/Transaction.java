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
    private int transaction_id;
    private String transaction_type;
    private float amount;
    private LocalDateTime timestamp; // changed here to LocalDateTime
    @Column(name = "from_account")
    private int fromAccount;

    @Column(name = "to_account")
    private int toAccount;

    private int initiated_by_user;

    @ManyToOne
    @JoinColumn(name = "from_account", insertable = false, updatable = false)
    private Account fromAccountEntity;

    @ManyToOne
    @JoinColumn(name = "to_account", insertable = false, updatable = false)
    private Account toAccountEntity;
}