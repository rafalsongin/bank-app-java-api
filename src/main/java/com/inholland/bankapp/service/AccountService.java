package com.inholland.bankapp.service;

import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.model.AccountType;
import com.inholland.bankapp.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final float DEFAULT_ABSOLUTE_TRANSFER_LIMIT = 10000;
    private static final float DEFAULT_DAILY_TRANSFER_LIMIT = 1000;
    private static final int ACCOUNT_NUMBER_LENGTH = 10;

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
        Optional<Account> existingAccount = accountRepository.findAccountByIBAN(IBAN);
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

    public List<Account> getAccountsByCustomerId(Integer customerId) {
        return accountRepository.findAccountsByCustomerId(customerId);
    }

    public Optional<Account> getAccountByIBAN(String accountIban) {
        return accountRepository.findAccountByIBAN(accountIban);
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
}
