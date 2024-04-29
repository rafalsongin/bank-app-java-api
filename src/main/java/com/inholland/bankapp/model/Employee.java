package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Employee extends User {
    private String id; // Consider if this is necessary as `user_id` already exists in User
    private EmployeeRole role; // Specific to Employee

    public Employee(int user_id, String username, String email, String password, String userRole, String JWT, String firstName, String lastName, String id, EmployeeRole role) {
        super(user_id, username, email, password, userRole, JWT, firstName, lastName); // Call to User's constructor
        this.id = id; // Employee specific ID, if different from user_id
        this.role = role; // Employee specific role
    }
}
