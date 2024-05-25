package com.inholland.bankapp.controller;

import com.inholland.bankapp.service.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AtmController {
    
    @Autowired
    private AtmService atmService;
    
    
    
}
