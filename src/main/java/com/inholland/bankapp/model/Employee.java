package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "employee")
@PrimaryKeyJoinColumn(name = "user_id")
@Setter
@Getter
@NoArgsConstructor
public class Employee extends User{
    @Enumerated(EnumType.STRING)
    @Column(name = "employee_role")
    private EmployeeRole employeeRole;


}
