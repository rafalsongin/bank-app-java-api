package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("User")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    private String username;
    private String email;
    private String password;
    private boolean isEmployee;
    private String JWT;
    private String first_name;
    private String last_name;
    private int bank_id;

    // Custom constructor for easier object creation in your application logic
    public User(int user_id, String username, String email, String password, boolean isEmployee, String JWT, String first_name, String last_name, int bank_id) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isEmployee = isEmployee;
        this.JWT = JWT;
        this.first_name = first_name;
        this.last_name = last_name;
        this.bank_id = bank_id;
    }
}


