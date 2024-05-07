package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.CustomerRegistrationDto;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerNewUserAccount(CustomerRegistrationDto registrationDto) {
        /*Customer user = new Customer();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setUserRole(UserRole.CUSTOMER);
        user.setAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);*/
        
        Customer user = new Customer();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setUserRole(UserRole.CUSTOMER);
        user.setAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);
        return userRepository.save(user);
    }
}