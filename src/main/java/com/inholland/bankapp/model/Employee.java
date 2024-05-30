package com.inholland.bankapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee")
@PrimaryKeyJoinColumn(name = "user_id")
@Setter
@Getter
@NoArgsConstructor
public class Employee extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;
}
