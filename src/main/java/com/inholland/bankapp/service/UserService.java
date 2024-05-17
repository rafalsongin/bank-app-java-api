package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.dto.EmployeeRegistrationDto;
import com.inholland.bankapp.exceptions.InvalidDataException;
import com.inholland.bankapp.model.AccountApprovalStatus;
import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.model.User;
import com.inholland.bankapp.model.UserRole;
import com.inholland.bankapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
        
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}