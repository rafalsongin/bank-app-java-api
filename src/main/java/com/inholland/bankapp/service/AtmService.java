package com.inholland.bankapp.service;

import com.inholland.bankapp.repository.AtmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtmService {
    
    @Autowired
    private AtmRepository atmRepository;

}
