package com.inholland.bankapp.unit_testing.model;

import com.inholland.bankapp.model.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    Account account;

    @Test
    void newAccountShouldNotBeNull() {
        account = new Account();
        assertNotNull(account);
    }

    @Test
    void accountBalanceShouldNotBeLessThanZero() {
        account = new Account();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.setBalance(-1));
        assertEquals("Balance cannot be less than zero", exception.getMessage());
    }

    @Test
    void accountAbsoluteTransferLimitShouldNotBeLessThanZero() {
        account = new Account();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.setAbsoluteTransferLimit(-1));
        assertEquals("Absolute transfer limit cannot be less than zero", exception.getMessage());
    }

    @Test
    void accountDailyTransferLimitShouldNotBeLessThanZero() {
        account = new Account();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.setDailyTransferLimit(-1));
        assertEquals("Daily transfer limit cannot be less than zero", exception.getMessage());
    }


    @Test
    void accountTypeShouldNotBeNull() {
        account = new Account();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.setAccountType(null));
        assertEquals("Account type cannot be null", exception.getMessage());
    }

    @Test
    void accountIBANShouldNotBeNull() {
        account = new Account();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.setIBAN(null));
        assertEquals("IBAN cannot be null", exception.getMessage());
    }



}