package com.inholland.bankapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private int user_id;
    private String username;
    private String email;
    private String password;
    private String role;
    private String JWT;
    private String first_name;
    private String last_name;

    public User (int user_id, String username, String email, String password, String role, String JWT, String first_name, String last_name) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.JWT = JWT;
        this.first_name = first_name;
        this.last_name = last_name;
    }


}


