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

    @Column(name ="BSN_number")
    private String BSN;
    @Column(name = "phone_number")
    private String phoneNumber;


    @Enumerated(EnumType.STRING)
    @Column(name = "account_approval_status")
    private AccountApprovalStatus accountApprovalStatus;
    @Column(name = "transaction_limits")
    private float transactionLimits;

}

