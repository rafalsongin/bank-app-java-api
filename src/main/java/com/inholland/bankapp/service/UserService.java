package com.inholland.bankapp.service;

import com.inholland.bankapp.model.User;
import com.inholland.bankapp.model.UserRole;
import com.inholland.bankapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
        
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User getUserById(int initiatedByUser) {
        return userRepository.findById(initiatedByUser).orElse(null);
    }

    public boolean isAccountOwner(String email, String iban) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return userRepository.isAccountOwner(iban, user.getUserId());
        }
        return false;
    }

    public UserRole getUserRoleByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get().getUserRole(); 
        }
        return null;
    }
}