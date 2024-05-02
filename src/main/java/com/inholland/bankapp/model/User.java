package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userID;

    private String username;
    private String email;
    private String password;
    @Column (name = "is_employee")
    private boolean isEmployee;
    private String JWT;
    @Column (name ="first_name")
    private String firstName;
    @Column (name = "last_name")
    private String lastName;
    @Column (name = "bank_id")
    private int bankID;

}


