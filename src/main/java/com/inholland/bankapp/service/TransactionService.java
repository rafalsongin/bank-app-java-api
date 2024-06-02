package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inholland.bankapp.repository.TransactionRepository;
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

    private String errorMessageStart = "[Error] Transaction-";

    /**
     Get Method - gets all transactions
     @return    - returns all existing transactions
     */
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) {
        return repository.findTransactionsByAccountId(accountId);
    }

    /**
     Save Method - saves transaction to the database
     @param transactionDto  - parameter is an transactionDto class, that represents a transaction as DTO (Data Transfer Object)
     @return    - returns the created transactionDto with a timestamp from database, if incorrect returns 'null'
     */
    public TransactionDto saveTransaction(TransactionDto transactionDto) {
        try{
            if(!isTransactionValid(transactionDto)){
                throw new RuntimeException("CreateTransaction: Transaction is not valid!");
            }

            // Update both account balances
            Account fromAccount = accountService.getCheckingAccountByIBAN(transactionDto.getFromAccount());
            Account toAccount = accountService.getAccountByIBAN(transactionDto.getToAccount()).get();
            updateFromAndToAccountBalances(transactionDto.getAmount(), fromAccount, toAccount);

            // Save transaction
            Transaction transaction = transformTransaction(transactionDto);
            repository.save(transaction);

            // Overwrite transactionDto with a transaction containing timestamp from Database
            transactionDto = transformTransactionDTO(transaction);
        }catch (Exception e){
            System.out.println(errorMessageStart + e.getMessage());
            return null;
        }
        return transactionDto;
    }

    /**
     Boolean Method - checks transaction validity
     @param transactionDto  - parameter is an transactionDto class, that represents a transaction as DTO (Data Transfer Object)
     @return    - returns 'true' if transaction is good/correct, 'false' if transaction is bad/incorrect
     */
    private boolean isTransactionValid(TransactionDto transactionDto) {
        try{
            Optional<Account> optFromAccount = accountService.getAccountByIBAN(transactionDto.getFromAccount());
            Optional<Account> optToAccount = accountService.getAccountByIBAN(transactionDto.getToAccount());
            // Check if both accounts exits
            if(!optFromAccount.isPresent() || !optToAccount.isPresent()){
                throw new RuntimeException("SaveValidation: Sender or Recipient accounts not found!");
            }

            float balanceAfterThisTransaction = optFromAccount.get().getBalance()-transactionDto.getAmount();
            // Check if account has reached the absolute limit
            if(balanceAfterThisTransaction < optFromAccount.get().getAbsoluteTransferLimit()){
                throw new RuntimeException(errorMessageStart + "SaveValidation: Absolute Limit reached!");
            }

            // Check if account has reached daily transfer limit
            Float dailyTransferTotal = repository.getAccountTotalDailyTransferAmount(optFromAccount.get().getAccountId());
            if(dailyTransferTotal+transactionDto.getAmount() > optFromAccount.get().getDailyTransferLimit()){
                throw new RuntimeException(errorMessageStart + "SaveValidation: Daily Transfer Limit reached!");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
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
}

