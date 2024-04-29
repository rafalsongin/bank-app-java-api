package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Setter
@Getter
@Entity
public class Employee extends User{
    private int employee_id;
    private EmployeeRole role;

    public Employee(int employee_id, int user_id, String username, String email, String password, boolean isEmployee, String JWT, String firstName, String lastName, int bank_id, EmployeeRole role) {
        super(user_id, username, email, password, isEmployee, JWT, firstName, lastName, bank_id);
        this.employee_id = employee_id;
        this.role = role;
    }

}
