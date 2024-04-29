package com.inholland.bankapp.service;

import com.inholland.bankapp.model.User;
import com.inholland.bankapp.repository.BankRepository;
import com.inholland.bankapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inholland.bankapp.model.Bank;

import java.util.List;
import java.util.Optional;


@Service
public class BankService {

        @Autowired
        private BankRepository bankRepository;

        public List<Bank> getAllBanks() {
            return bankRepository.findAll();
        }


}
