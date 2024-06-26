package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.AccountDto;
import com.inholland.bankapp.exceptions.CustomerAccountsNotFoundException;
import com.inholland.bankapp.exceptions.CustomerNotFoundException;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.model.AccountType;
import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.model.Transaction;
import com.inholland.bankapp.repository.AccountRepository;
import com.inholland.bankapp.repository.CustomerRepository;
import com.inholland.bankapp.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    // <editor-fold desc="Initialization components">
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    // </editor-fold

    // <editor-fold desc="Finals for generating IBAN.">
    private static final SecureRandom random = new SecureRandom();
    private static final String BANK_CODE = "INHO0"; // Your bank's code
    private static final String COUNTRY_CODE = "NL";
    private static final float DEFAULT_BALANCE = 0;
    private static final float DEFAULT_ABSOLUTE_TRANSFER_LIMIT = 0;
    private static final float DEFAULT_DAILY_TRANSFER_LIMIT = 1000;
    private static final int ACCOUNT_NUMBER_LENGTH = 9;
    // </editor-fold>

    // <editor-fold desc="Methods for creating the accounts and IBAN.">
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

    private void createSavingsAccount(int customerID) {
        try {
            AccountType typeOfAccount = AccountType.SAVINGS;
            String uniqueIBAN = generateUniqueIBAN();
            createAccount(customerID, uniqueIBAN, typeOfAccount, DEFAULT_BALANCE, DEFAULT_ABSOLUTE_TRANSFER_LIMIT, DEFAULT_DAILY_TRANSFER_LIMIT);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating savings account");
        }
    }

    private void createCheckingAccount(int customerID) {
        try {
            AccountType typeOfAccount = AccountType.CHECKING;
            String uniqueIBAN = generateUniqueIBAN();
            createAccount(customerID, uniqueIBAN, typeOfAccount, DEFAULT_BALANCE, DEFAULT_ABSOLUTE_TRANSFER_LIMIT, DEFAULT_DAILY_TRANSFER_LIMIT);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating checking account");
        }
    }

    public void createAccounts(int customerId) {
        createSavingsAccount(customerId);
        createCheckingAccount(customerId);
    }
    // </editor-fold>

    // <editor-fold desc="Get accounts methods.">
    public AccountDto getCheckingAccountByIBAN(String IBAN) {
        Optional<Account> account = accountRepository.findByIBAN(IBAN);
        if (account.isPresent() && account.get().getAccountType() == AccountType.CHECKING) {
            return transformAccountToAccountDto(account.get());
        } else if (account.isPresent() && account.get().getAccountType() == AccountType.SAVINGS) {
            throw new IllegalArgumentException("Account is not a checking account");
        }
        return null;
    }

    public Optional<Account> getAccountByIBAN(String accountIban) {
        return accountRepository.findByIBAN(accountIban);
    }

    public Optional<Account> getAccountById(Integer accountId) {
        return accountRepository.findById(accountId);
    }

    public List<AccountDto> getAccountsByCustomerId(int customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundException(customerId);
        }

        List<Account> accounts = accountRepository.getAccountsByCustomerId(customerId);
        if (accounts.isEmpty()) {
            throw new CustomerAccountsNotFoundException(customerId);
        }

        return transformAccountToAccountDtoLoop(accounts);
    }
    // </editor-fold>

    // <editor-fold desc="Update Methods">
    public AccountDto updateAccount(String accountIban, AccountDto updatedAccount) {
        Optional<Account> account = accountRepository.findByIBAN(accountIban);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account not found");
        }
        if(updatedAccount.getAbsoluteTransferLimit() < -1000) {
            throw new IllegalArgumentException("Absolute transfer limit cannot be less than -1000");
        }
        if(updatedAccount.getDailyTransferLimit() < 100) {
            throw new IllegalArgumentException("Daily transfer limit cannot be less than 100");
        }
        Account existingAccount = account.get();
        existingAccount.setBalance(updatedAccount.getBalance());
        existingAccount.setAbsoluteTransferLimit(updatedAccount.getAbsoluteTransferLimit());
        existingAccount.setDailyTransferLimit(updatedAccount.getDailyTransferLimit());
        try {
            return transformAccountToAccountDto(accountRepository.save(existingAccount));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating account");
        }
    }

    /**
     * Update Method - update balances of 'sender' and 'retriever' accounts
     *
     * @param fromAccount - parameter is of Account class, that represents the account of the customer
     * @param toAccount   - parameter is of Account class, that represents the account of the customer
     */
    @Transactional
    public void updateTransferBalances(Account fromAccount, Account toAccount) {
        accountRepository.updateAccountBalances(fromAccount.getAccountId(), fromAccount.getBalance(), toAccount.getAccountId(), toAccount.getBalance());
    }
    // </editor-fold>

    // <editor-fold desc="ATM methods.">
    public Double findCheckingAccountBalanceByEmail(String email) {
        return accountRepository.findCheckingAccountBalanceByEmail(email);
    }

    @Transactional
    public void depositToCheckingAccount(String email, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        
        System.out.println("Email: " + email);

        Integer accountId = accountRepository.getCheckingAccountIdByEmail(email);
        if (accountId == null) {
            throw new IllegalArgumentException("Checking account not found for email: " + email);
        }

        transformAtmTransactionIntoTransaction(accountId, amount, "ATM DEPOSIT");

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

        int accountId = accountRepository.getCheckingAccountIdByEmail(email);
        transformAtmTransactionIntoTransaction(accountId, amount, "ATM WITHDRAW");

        accountRepository.withdrawFromCheckingAccount(email, amount);
    }

    private void transformAtmTransactionIntoTransaction(int accountId, double amountTemp, String transactionType) {
        Transaction transaction = new Transaction();

        // convert amount into float
        float amount = (float) amountTemp;

        // get user by account id
        int userId = accountRepository.getUserIdByAccountId(accountId);

        // Set transaction properties
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setInitiatedByUser(userId);
        // set current timestamp
        transaction.setTimestamp(LocalDateTime.now());
        // Retrieve and set account IDs
        transaction.setFromAccount(accountId);
        transaction.setToAccount(accountId);

        transactionRepository.save(transaction);
    }
    // </editor-fold>

    // <editor-fold desc="DTO transformation methods.">
    private List<AccountDto> transformAccountToAccountDtoLoop(List<Account> accounts) {
        List<AccountDto> accountsDto = new java.util.ArrayList<>();
        for (Account account : accounts) {
            accountsDto.add(transformAccountToAccountDto(account));
        }
        return accountsDto;
    }

    public AccountDto transformAccountToAccountDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(account.getAccountId());
        accountDto.setIBAN(account.getIBAN());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setBalance(account.getBalance());
        accountDto.setAbsoluteTransferLimit(account.getAbsoluteTransferLimit());
        accountDto.setDailyTransferLimit(account.getDailyTransferLimit());
        Customer owner = customerRepository.findById(account.getCustomerId()).get();

        String ownerFullName = owner.getFirstName() + " " + owner.getLastName();

        accountDto.setCustomerFullName(ownerFullName);
        accountDto.setAvailableDailyAmountForTransfer(account.getAvailableDailyAmountForTransfer());
        return accountDto;
    }
    // </editor-fold>
}