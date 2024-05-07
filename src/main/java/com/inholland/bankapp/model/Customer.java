package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "customer")
@PrimaryKeyJoinColumn(name = "user_id")
@Setter
@Getter
@NoArgsConstructor
public class Customer extends User {
    private String BSN_number;
    private String phone_number;
    private String account_approval_status;
    private Float transaction_limits;
}

