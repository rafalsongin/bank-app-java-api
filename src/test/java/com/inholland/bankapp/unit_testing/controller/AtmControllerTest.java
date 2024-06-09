package com.inholland.bankapp.controller;

import com.inholland.bankapp.dto.AtmTransactionDto;
import com.inholland.bankapp.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AtmControllerTest {

    @InjectMocks
    private AtmController atmController;

    @Mock
    private AccountService accountService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetCheckingAccountBalance_Success() {
        String email = "igmas@gmail.com";
        double expectedBalance = 100.0;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(accountService.findCheckingAccountBalanceByEmail(email)).thenReturn(expectedBalance);

        ResponseEntity<?> response = atmController.getCheckingAccountBalance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBalance, response.getBody());
    }

    @Test
    public void testGetCheckingAccountBalance_Exception() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = atmController.getCheckingAccountBalance();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred fetching balance", response.getBody());
    }

    @Test
    public void testDepositToCheckingAccount_Success() {
        String email = "igmas@gmail.com";
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(50.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        ResponseEntity<?> response = atmController.depositToCheckingAccount(atmTransactionDto);

        verify(accountService, times(1)).depositToCheckingAccount(email, atmTransactionDto.getAmount());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deposit was successful", response.getBody());
    }

    @Test
    public void testDepositToCheckingAccount_BadRequest() {
        String email = "igmas@gmail.com";
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(-50.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        doThrow(new IllegalArgumentException("Invalid amount")).when(accountService).depositToCheckingAccount(email, atmTransactionDto.getAmount());

        ResponseEntity<?> response = atmController.depositToCheckingAccount(atmTransactionDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid amount", response.getBody());
    }

    @Test
    public void testWithdrawFromCheckingAccount_Success() {
        String email = "igmas@gmail.com";
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(-50.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        ResponseEntity<?> response = atmController.withdrawFromCheckingAccount(atmTransactionDto);

        verify(accountService, times(1)).withdrawFromCheckingAccount(email, atmTransactionDto.getAmount());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Withdraw was successful", response.getBody());
    }

    @Test
    public void testWithdrawFromCheckingAccount_BadRequest() {
        String email = "igmas@gmail.com";
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(50.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        doThrow(new IllegalArgumentException("Invalid amount")).when(accountService).withdrawFromCheckingAccount(email, atmTransactionDto.getAmount());

        ResponseEntity<?> response = atmController.withdrawFromCheckingAccount(atmTransactionDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid amount", response.getBody());
    }
}
