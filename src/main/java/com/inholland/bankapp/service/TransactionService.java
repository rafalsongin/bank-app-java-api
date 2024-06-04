package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inholland.bankapp.repository.TransactionRepository;

import java.util.*;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    /**
     Get Method - gets all transactions
     @return    - returns all existing transactions
     */

    public Page<TransactionDto> getAllTransactions(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        Page<Transaction> transactions = repository.findAll(pageRequest);
        return transactions.map(this::transformTransactionDTO);
    }


    public List<Transaction> getTransactionsByAccountId(int accountId) {
        return repository.findTransactionsByAccountId(accountId);
    }

    /**
     Save Method - saves transaction to the database
     @param transactionDto  - parameter is an transactionDto class, that represents a transaction as DTO (Data Transfer Object)
     @return    - returns the created transaction
     */
    public TransactionDto saveTransaction(TransactionDto transactionDto) {


        Optional<Account> optFromAccount = accountService.getAccountByIBAN(transactionDto.getFromAccount());
        Optional<Account> optToAccount = accountService.getAccountByIBAN(transactionDto.getToAccount());
        if(!optFromAccount.isPresent() || !optToAccount.isPresent()){
            throw new RuntimeException("[Error] CreateTransactionDTO: From or To accounts not found!");
        }

        // Check if from_account has sufficient balance and update accounts, if it does
        if (optFromAccount.get().getBalance() < transactionDto.getAmount()) {
            throw new RuntimeException("Insufficient balance in the from account.");
        }

        updateFromAndToAccountBalances(transactionDto.getAmount(), optFromAccount.get(), optToAccount.get());

        // Create, save and return transaction
        Transaction transaction = transformTransaction(transactionDto);
        repository.save(transaction);

        return transformTransactionDTO(transaction);
    }

    /**
     Transform Method - transforms a transactionDto object to a transaction object
     @param transactionDto  - parameter is an transactionDto class, that represents a transaction as DTO (Data Transfer Object)
     @return    - returns transaction object
     */
    private Transaction transformTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();

        // Check if accounts from the transaction exist
        Optional<Account> optFromAccount = accountService.getAccountByIBAN(transactionDto.getFromAccount());
        Optional<Account> optToAccount = accountService.getAccountByIBAN(transactionDto.getToAccount());
        if(!optFromAccount.isPresent() || !optToAccount.isPresent()){
            throw new RuntimeException("[Error] CreateTransactionDTO: From or To accounts not found!");
        }

        // Set transaction properties
        transaction.setTransactionType(transactionDto.getTransactionType());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setInitiatedByUser(transactionDto.getInitiatedByUser());
        // set current timestamp
        transaction.setTimestamp(LocalDateTime.now());
        // Retrieve and set account IDs
        transaction.setFromAccount(optFromAccount.get().getAccountId());
        transaction.setToAccount(optToAccount.get().getAccountId());

        return transaction;
    }

    /**
     Transform Method - transforms a transaction object to a transactionDto object
     @param transaction  - parameter is a transaction class, which is used in the back end for transactions
     @return    - returns transactionCreationDto object
     */
    private TransactionDto transformTransactionDTO(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();

        // Check if accounts from the transaction exist
        Optional<Account> optFromAccount = accountService.getAccountById(transaction.getFromAccount());
        Optional<Account> optToAccount = accountService.getAccountById(transaction.getToAccount());
        if(!optFromAccount.isPresent() || !optToAccount.isPresent()){
            throw new RuntimeException("[Error] CreateTransactionDTO: From or To accounts not found!");
        }

        // Set transactionDto properties
        transactionDto.setTransactionType(transaction.getTransactionType());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setTimestamp(transaction.getTimestamp().toString());
        transactionDto.setFromAccount(optFromAccount.get().getIBAN());
        transactionDto.setToAccount(optToAccount.get().getIBAN());
        transactionDto.setInitiatedByUser(transaction.getInitiatedByUser());
        User user = userService.getUserById(transaction.getInitiatedByUser());
        transactionDto.setInitiatorName(user.getFirstName() + " " + user.getLastName());
        transactionDto.setInitiatorRole(user.getUserRole().toString().toLowerCase());

        return transactionDto;
    }

    /**
     Update Method - updates From account and To accounts in the database with the amount that was transferred between them
     @param amount  - parameter is a Float type, that represents an amount that will be transferred
     @param fromAccount   - parameter is an Account class, used for updating from_account balance
     @param toAccount   - parameter is an Account class, used for updating to_account balance
     */
    private void updateFromAndToAccountBalances(Float amount, Account fromAccount, Account toAccount){
        // Update account balances
        fromAccount.setBalance(fromAccount.getBalance() - amount); // decrease from_account balance
        toAccount.setBalance(toAccount.getBalance() + amount); // increase to_account balance

        // Save updated account balances
        accountService.updateTransferBalances(fromAccount, toAccount);
    }

    /**
     Get Method - gets all transactions where iban is either as a sender or retriever
     @param iban  - parameter is a String type, which is used in get the transactions
     @return    - returns a list of transactions where each transaction is an TransactionDto object
     */
    public List<TransactionDto> getAllTransactionsByIban(String iban) {
        Optional<Account> account = accountService.getAccountByIBAN(iban);

        if(!account.isPresent()){
            throw new RuntimeException("Account was not found!");
        }

        List<Transaction> transactions = repository.findTransactionsByAccountId(account.get().getAccountId());

        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction transaction:
             transactions) {
            transactionDtos.add(this.transformTransactionDTO(transaction));
        }

        return transactionDtos;
    }

    public List<TransactionDto> getAllTransactionsByAccountId(Integer accountId) {
        List<Transaction> transactions = repository.findTransactionsByAccountId(accountId);

        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction transaction:
                transactions) {
            transactionDtos.add(this.transformTransactionDTO(transaction));
        }

        return transactionDtos;
    }

    public List<TransactionDto> getCustomerTransactions(int customerID) {
        User user = userService.getUserById(customerID);
        if (user!= null && user.getUserRole().equals(UserRole.CUSTOMER)) {
            List<Account> accounts = accountService.getAccountsByCustomerId(customerID);
            if (accounts.isEmpty()) {
                throw new IllegalArgumentException("Customer has no accounts");
            }
            List<Transaction> transactions = new ArrayList<>();
            for (Account account : accounts) {
                transactions.addAll(repository.findTransactionsByAccountId(account.getAccountId()));
            }
            transactions.sort(Comparator.comparing(Transaction::getTimestamp));

            List<TransactionDto> transactionDtos = new ArrayList<>();
            for (Transaction transaction:
                    transactions) {
                transactionDtos.add(this.transformTransactionDTO(transaction));
            }

            return transactionDtos;
        }
        else {
            throw new IllegalArgumentException("Customer not found");
        }
    }
}