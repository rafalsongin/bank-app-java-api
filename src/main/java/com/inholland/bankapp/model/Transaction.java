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
    private int from_account;
    private int to_account;
    private int initiated_by_user;
}
