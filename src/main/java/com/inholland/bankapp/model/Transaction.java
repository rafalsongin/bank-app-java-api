package com.inholland.bankapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transaction")
@Setter
@Getter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transaction_id;

    @Column(name = "transaction_type")
    private String transaction_type;

    @Column(name = "amount")
    private float amount;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "from_account")
    private int from_account;

    @Column(name = "to_account")
    private int to_account;

    @Column(name = "initiated_by_user")
    private int initiated_by_account;
}
