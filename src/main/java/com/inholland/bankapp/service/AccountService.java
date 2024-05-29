package com.inholland.bankapp.service;

import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.model.AccountType;
import com.inholland.bankapp.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private static final SecureRandom random = new SecureRandom();
    private static final String BANK_CODE = "INHO0"; // Your bank's code
    private static final String COUNTRY_CODE = "NL";
    private static final float DEFAULT_BALANCE = 0;
    private static final float DEFAULT_ABSOLUTE_TRANSFER_LIMIT = 0;
    private static final float DEFAULT_DAILY_TRANSFER_LIMIT = 1000;
    private static final int ACCOUNT_NUMBER_LENGTH = 9;

    public String generateUniqueIBAN() {
        String baseIBAN = COUNTRY_CODE + "xx" + BANK_CODE; // Placeholder for check digits
        while (true) {
            String accountNumber = generateAccountNumber();
            String uniqueIBAN = baseIBAN + accountNumber;
            uniqueIBAN = computeCheckDigits(uniqueIBAN); // Implement this method based on the IBAN standard

            if (isIBANUnique(uniqueIBAN)) {
                return uniqueIBAN;
            }
        }
    }

    private boolean isIBANUnique(String IBAN) {
        Optional<Account> existingAccount = accountRepository.findByIBAN(IBAN);
        return existingAccount.isEmpty();
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // Dummy implementation - replace this with the actual check digit computation
    private String computeCheckDigits(String iban) {
        // Replace 'xx' with computed check digits based on the IBAN standard
        return iban.replace("xx", "00"); // Simplified for example purposes
    }

    public void createAccount(int customerId, String IBAN, AccountType accountType, float balance, float absoluteTransferLimit, float dailyTransferLimit) {
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setIBAN(IBAN);
        account.setAccountType(accountType);
        account.setBalance(balance);
        account.setAbsoluteTransferLimit(absoluteTransferLimit);
        account.setDailyTransferLimit(dailyTransferLimit);
        
        // Save the new account
        accountRepository.save(account);
    }

    private void createSavingsAccount(int customerID){
        try{
            AccountType typeOfAccount = AccountType.SAVINGS;
            String uniqueIBAN = generateUniqueIBAN();
            createAccount(customerID, uniqueIBAN, typeOfAccount, DEFAULT_BALANCE, DEFAULT_ABSOLUTE_TRANSFER_LIMIT, DEFAULT_DAILY_TRANSFER_LIMIT);
        }
        catch (Exception e){
            //e.printStackTrace();
        }
    }

    private void createCheckingAccount(int customerID){
        try{
            AccountType typeOfAccount = AccountType.CHECKING;
            String uniqueIBAN =generateUniqueIBAN();
            createAccount(customerID, uniqueIBAN, typeOfAccount, DEFAULT_BALANCE, DEFAULT_ABSOLUTE_TRANSFER_LIMIT, DEFAULT_DAILY_TRANSFER_LIMIT);
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void createAccounts(int customerId) {
        createSavingsAccount(customerId);
        createCheckingAccount(customerId);
    }


    public List<Account> getAccountsByCustomerId(int customer_id){
        return accountRepository.getAccountsByCustomerId(customer_id);
    }

    public Account updateAccount(int accountId, Account updatedAccount) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            return null;
        }
        Account existingAccount = account.get();
        existingAccount.setBalance(updatedAccount.getBalance());
        existingAccount.setAbsoluteTransferLimit(updatedAccount.getAbsoluteTransferLimit());
        existingAccount.setDailyTransferLimit(updatedAccount.getDailyTransferLimit());
        return accountRepository.save(existingAccount);
    }

    public Account getCheckingAccountByIBAN(String IBAN) {
        Optional<Account> account = accountRepository.findByIBAN(IBAN);
        if (account.isPresent() && account.get().getAccountType() == AccountType.CHECKING) {
            return account.get();
        }
        return null;
    }


    public Optional<Account> getAccountByIBAN(String accountIban) {
        return accountRepository.findByIBAN(accountIban);
    }

    public Optional<Account> getAccountById(Integer accountId) {
        return accountRepository.findById(accountId);
    }

    /**
     Update Method - update an account by passing an Account object
     @param account  - parameter is of Account class, that represents the account of the customer
     @return    - returns the account, if account parameter is provided.
     */
    public Account updateAccount(Account account){
        return accountRepository.save(account);
    }

    /**
     Update Method - update balances of 'sender' and 'retriever' accounts
     @param fromAccount  - parameter is of Account class, that represents the account of the customer
     @param toAccount  - parameter is of Account class, that represents the account of the customer
     */
    @Transactional
    public void updateTransferBalances(Account fromAccount, Account toAccount) {
        accountRepository.updateAccountBalances(fromAccount.getAccountId(), fromAccount.getBalance(), toAccount.getAccountId(), toAccount.getBalance());
    }

    public double findCheckingAccountBalanceByEmail(String email) {
        return accountRepository.findCheckingAccountBalanceByEmail(email);
    }
    
    @Transactional
    public void depositToCheckingAccount(String email, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        
        accountRepository.depositToCheckingAccount(email, amount);
    }

    @Transactional
    public void withdrawFromCheckingAccount(String email, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        double currentBalance = accountRepository.findCheckingAccountBalanceByEmail(email);
        if (currentBalance < amount) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }

        accountRepository.withdrawFromCheckingAccount(email, amount);
    }
}

