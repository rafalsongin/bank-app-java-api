package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.TransactionCreationDto;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.model.Transaction;
import com.inholland.bankapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    /*public List<Transaction> getAllTransactionsByFromAccountId(Integer accountId) {
        return repository.findAllTransactionsByFromAccountId(accountId);
    }*/

    /**
     Save Method - saves transaction to the database
     @param transactionCreationDto  - parameter is an transactionCreationDto class, that represents a transaction as DTO (Data Transfer Object)
     @return    - returns the created transaction
     */
    public TransactionCreationDto saveTransaction(TransactionCreationDto transactionCreationDto) {
        Optional<Account> fromAccountOptional = accountService.getAccountByIBAN(transactionCreationDto.getFrom_account());
        Optional<Account> toAccountOptional = accountService.getAccountByIBAN(transactionCreationDto.getTo_account());

        // Check if both from_account and to_account accounts exist within database
        if (!fromAccountOptional.isPresent() || !toAccountOptional.isPresent()) {
            throw new RuntimeException("One or both of the accounts do not exist.");
        }
        Account fromAccount = fromAccountOptional.get();
        Account toAccount = toAccountOptional.get();

        // Check if from_account has sufficient balance and update accounts, if it does
        if (fromAccount.getBalance() < transactionCreationDto.getAmount()) {
            throw new RuntimeException("Insufficient balance in the from account.");
        }
        updateFromAndToAccountBalances(transactionCreationDto.getAmount(), fromAccount, toAccount);

        // Create, save and return transaction
        Transaction transaction = createTransaction(transactionCreationDto, fromAccount.getAccountId(), toAccount.getAccountId());
        repository.save(transaction);

        return createTransactionDTO(transaction, fromAccount, toAccount);
    }

    /**
     Create Method - transforms a transactionCreationDto object to a transaction object
     @param transactionCreationDto  - parameter is an transactionCreationDto class, that represents a transaction as DTO (Data Transfer Object)
     @param fromAccountId   - parameter is an int type, used to set from_account to transaction object
     @param toAccountId   - parameter is an int type, used to set to_account to transaction object
     @return    - returns transaction object
     */
    private Transaction createTransaction(TransactionCreationDto transactionCreationDto, Integer fromAccountId, Integer toAccountId) {
        Transaction transaction = new Transaction();

        // Set transaction properties
        transaction.setTransaction_type(transactionCreationDto.getTransaction_type());
        transaction.setAmount(transactionCreationDto.getAmount());
        transaction.setInitiated_by_account(transactionCreationDto.getInitiated_by_account());

        // set current timestamp
        transaction.setTimestamp(LocalDateTime.now().toString());

        // Retrieve and set account IDs
        transaction.setFrom_account(fromAccountId);
        transaction.setTo_account(toAccountId);

        return transaction;
    }

    /**
     Create Method - transforms a transaction object to a transactionCreationDto object
     @param transaction  - parameter is an transactionCreationDto class, that represents a transaction as DTO (Data Transfer Object)
     @param fromAccount   - parameter is an Account class, used to set iban for from_account
     @param toAccount   - parameter is an Account class, used to set iban for to_account
     @return    - returns transactionCreationDto object
     */
    private TransactionCreationDto createTransactionDTO(Transaction transaction, Account fromAccount, Account toAccount) {
        TransactionCreationDto transactionDto = new TransactionCreationDto();

        // Set transaction properties
        transactionDto.setTransaction_type(transaction.getTransaction_type());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setTimestamp(transaction.getTimestamp());
        transactionDto.setFrom_account(fromAccount.getIBAN());
        transactionDto.setTo_account(toAccount.getIBAN());
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
        accountService.updateAccount(fromAccount);
        accountService.updateAccount(toAccount);
    }
}
