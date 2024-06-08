package com.inholland.bankapp.unit_testing.controller;

import com.inholland.bankapp.controller.AccountController;
import com.inholland.bankapp.dto.AccountDto;
import com.inholland.bankapp.exceptions.CustomerAccountsNotFoundException;
import com.inholland.bankapp.exceptions.CustomerNotFoundException;
import com.inholland.bankapp.model.AccountType;
import com.inholland.bankapp.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    // <editor-fold desc="Initialization components">
    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;
    // </editor-fold>

    // <editor-fold desc="Global Test Details">
    final private String apiBaseEndpoint = "http://localhost:8080/api/accounts/";
    /*String apiEndpoint = apiBaseEndpoint + "parameter/value"; */ // copy and replace, for tests requiring API

    //</editor-fold>

    // <editor-fold desc="Test getAccountsByCustomerId">
    @Test
    // Ignas
    void testGetAccountsByCustomerId() throws Exception {
        // Given customerId and data about accounts
        final Integer customerId = 34;

        // ! Some values might change depending on the current state of the accounts in the database.
        AccountDto account1 = this.makeAccountDto(73,
                "NL00INHO0766828063", AccountType.SAVINGS,
                0.0f, 0.0f, 1000.0f,
                "tester testington", 1000.0f);
        AccountDto account2 = this.makeAccountDto(74,
                "NL00INHO0368456732", AccountType.CHECKING,
                762.0f, 0.0f, 1000.0f,
                "tester testington", -10.0f);
        List<AccountDto> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);

        // Mock the behavior of the accountService
        when(accountService.getAccountsByCustomerId(customerId)).thenReturn(accounts);

        // Begin the test:
        // When
        ResponseEntity<List<AccountDto>> response = accountController.getAccountsByCustomerId(customerId);

        // Then
        assertEquals(200, response.getStatusCode().value());
        List<AccountDto> responseBody = response.getBody();
        assertEquals(2, responseBody.size());

        AccountDto responseAccount1 = responseBody.get(0);
        assertEquals(account1.getAccountId(), responseAccount1.getAccountId());
        assertEquals(account1.getIBAN(), responseAccount1.getIBAN());
        assertEquals(account1.getAccountType(), responseAccount1.getAccountType());
        assertEquals(account1.getBalance(), responseAccount1.getBalance());
        assertEquals(account1.getAbsoluteTransferLimit(), responseAccount1.getAbsoluteTransferLimit());
        assertEquals(account1.getDailyTransferLimit(), responseAccount1.getDailyTransferLimit());
        assertEquals(account1.getCustomerFullName(), responseAccount1.getCustomerFullName());
        assertEquals(account1.getAvailableDailyAmountForTransfer(), responseAccount1.getAvailableDailyAmountForTransfer());

        AccountDto responseAccount2 = responseBody.get(1);
        assertEquals(account2.getAccountId(), responseAccount2.getAccountId());
        assertEquals(account2.getIBAN(), responseAccount2.getIBAN());
        assertEquals(account2.getAccountType(), responseAccount2.getAccountType());
        assertEquals(account2.getBalance(), responseAccount2.getBalance());
        assertEquals(account2.getAbsoluteTransferLimit(), responseAccount2.getAbsoluteTransferLimit());
        assertEquals(account2.getDailyTransferLimit(), responseAccount2.getDailyTransferLimit());
        assertEquals(account2.getCustomerFullName(), responseAccount2.getCustomerFullName());
        assertEquals(account2.getAvailableDailyAmountForTransfer(), responseAccount2.getAvailableDailyAmountForTransfer());
    }

    @Test
        // Ignas
    void testGetAccountsByCustomerId_noContent() {
        // Given customerId that does not have accounts
        final Integer customerId = 33;
        // Mock the behavior of the accountService
        when(accountService.getAccountsByCustomerId(customerId)).thenThrow(new CustomerAccountsNotFoundException(customerId));

        // When
        ResponseEntity<List<AccountDto>> response = accountController.getAccountsByCustomerId(customerId);
        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
        // Ignas
    void testGetAccountsByCustomerId_notFound() {
        // Given customerId that doesn't exist
        final Integer customerId = 1000;
        // Mock the behavior of the accountService
        when(accountService.getAccountsByCustomerId(customerId)).thenThrow(new CustomerNotFoundException(customerId));

        // When
        ResponseEntity<List<AccountDto>> response = accountController.getAccountsByCustomerId(customerId);
        // Then
        assertEquals(404, response.getStatusCodeValue());
    }
    // </editor-fold>

    // <editor-fold desc="Dto Constructors">
    private AccountDto makeAccountDto(Integer accountId, String iban, AccountType type, Float balance, Float absoluteLimit, Float dailyLimit, String fullName, Float availableDailyAmountForTransfer) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(2);
        accountDto.setIBAN("AccountBan2");
        accountDto.setAccountType(AccountType.SAVINGS);
        accountDto.setBalance(0.0f);
        accountDto.setAbsoluteTransferLimit(1.0f);
        accountDto.setDailyTransferLimit(1002.0f);
        accountDto.setCustomerFullName("Fname Lname");
        accountDto.setAvailableDailyAmountForTransfer(1002.0f);

        return accountDto;
    }
    // </editor-fold>
}
