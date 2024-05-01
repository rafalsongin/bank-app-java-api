package com.inholland.bankapp.service;

import com.inholland.bankapp.database.LoginRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    public LoginRepository loginRepository;
    
    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }
}
