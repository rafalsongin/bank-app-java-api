package com.inholland.bankapp.service;

import com.inholland.bankapp.repository.BankRepository;
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


    /**
     Get Method - gets a bank by bankId
     @param bankId  - parameter is an Integer type, which is used to get the bank
     @return    - returns an existing bank
     */
    public Optional<Bank> getBankById(Integer bankId){
        Optional<Bank> bank = bankRepository.findById(bankId);

        if(!bank.isPresent()){
            throw new RuntimeException("[Error] Bank not found!");
        }

        return bank;
    }
}
