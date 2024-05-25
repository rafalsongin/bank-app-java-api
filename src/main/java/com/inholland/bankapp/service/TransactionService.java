package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.model.Transaction;
import com.inholland.bankapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountService accountService;

    /**
     Get Method - gets all transactions
     @return    - returns all existing transactions
     */
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    /**
     Get Method - getting the transaction by id
     @param id  - parameter is an Integer type, that represents the id of the transaction
     @return    - returns the transaction, if id parameter is provided.
     */
    public Optional<Transaction> getTransactionById(Integer id) {
        return repository.findById(id);
    }

    /**
     Save Method - saves transaction to the database
     @param transactionDto  - parameter is an transactionDto class, that represents a transaction as DTO (Data Transfer Object)
     @return    - returns the created transaction
     */
    public TransactionDto saveTransaction(TransactionDto transactionDto) {

        Optional<Account> optFromAccount = accountService.getAccountByIBAN(transactionDto.getFrom_account());
        Optional<Account> optToAccount = accountService.getAccountByIBAN(transactionDto.getTo_account());
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
        Optional<Account> optFromAccount = accountService.getAccountByIBAN(transactionDto.getFrom_account());
        Optional<Account> optToAccount = accountService.getAccountByIBAN(transactionDto.getTo_account());
        if(!optFromAccount.isPresent() || !optToAccount.isPresent()){
            throw new RuntimeException("[Error] CreateTransactionDTO: From or To accounts not found!");
        }

        // Set transaction properties
        transaction.setTransaction_type(transactionDto.getTransaction_type());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setInitiated_by_account(transactionDto.getInitiated_by_account());
        // set current timestamp
        transaction.setTimestamp(LocalDateTime.now().toString());
        // Retrieve and set account IDs
        transaction.setFrom_account(optFromAccount.get().getAccountId());
        transaction.setTo_account(optToAccount.get().getAccountId());

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
        Optional<Account> optFromAccount = accountService.getAccountById(transaction.getFrom_account());
        Optional<Account> optToAccount = accountService.getAccountById(transaction.getTo_account());
        if(!optFromAccount.isPresent() || !optToAccount.isPresent()){
            throw new RuntimeException("[Error] CreateTransactionDTO: From or To accounts not found!");
        }

        // Set transactionDto properties
        transactionDto.setTransaction_type(transaction.getTransaction_type());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setTimestamp(transaction.getTimestamp());
        transactionDto.setFrom_account(optFromAccount.get().getIBAN());
        transactionDto.setTo_account(optToAccount.get().getIBAN());
        transactionDto.setInitiated_by_account(transaction.getInitiated_by_account());

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

        List<Transaction> transactions = repository.findAllById(account.get().getAccountId());

        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction transaction:
             transactions) {
            transactionDtos.add(this.transformTransactionDTO(transaction));
        }

        return transactionDtos;
    }
}
