package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bank_id;
    private String name;
    private String currency;

    public Bank(int bank_id, String name, String currency) {
        this.bank_id = bank_id;
        this.name = name;
        this.currency = currency;
    }
}