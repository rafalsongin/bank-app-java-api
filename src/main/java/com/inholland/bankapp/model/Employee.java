package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private EmployeeRole role;
    
    public Employee(String id, String firstName, String lastName, String email, EmployeeRole role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }
}
