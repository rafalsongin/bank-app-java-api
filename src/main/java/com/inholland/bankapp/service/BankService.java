package com.inholland.bankapp.service;

import com.inholland.bankapp.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inholland.bankapp.model.Bank;

import java.util.List;

@Service
public class BankService {

        @Autowired
        private BankRepository bankRepository;

        public List<Bank> getAllBanks() {
            return bankRepository.findAll();
        }
}
