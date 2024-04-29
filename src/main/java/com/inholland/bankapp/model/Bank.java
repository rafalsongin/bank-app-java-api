package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Bank {
    private int bank_id;
    private String name;
    private String currency;

    public Bank(int bank_id, String name, String currency) {
        this.bank_id = bank_id;
        this.name = name;
        this.currency = currency;
    }
}