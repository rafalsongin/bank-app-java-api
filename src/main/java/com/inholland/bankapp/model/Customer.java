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


    @Enumerated(EnumType.STRING)
    @Column(name = "account_approval_status")
    private AccountApprovalStatus accountApprovalStatus;
    private float transaction_limits;

}

