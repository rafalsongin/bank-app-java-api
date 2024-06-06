package com.inholland.bankapp.unit_testing.controller;

import com.inholland.bankapp.controller.AtmController;
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
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetCheckingAccountBalance_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("igmas@gmail.com");
        when(accountService.findCheckingAccountBalanceByEmail("igmas@gmail.com")).thenReturn(1000.0);

        ResponseEntity<?> response = atmController.getCheckingAccountBalance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1000.0, response.getBody());
    }

    @Test
    public void testGetCheckingAccountBalance_Exception() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("igmas@gmail.com");
        when(accountService.findCheckingAccountBalanceByEmail("igmas@gmail.com")).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = atmController.getCheckingAccountBalance();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred fetching balance", response.getBody());
    }

    @Test
    public void testDepositToCheckingAccount_Success() {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(500.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("igmas@gmail.com");

        doNothing().when(accountService).depositToCheckingAccount("igmas@gmail.com", 500.0);

        ResponseEntity<?> response = atmController.depositToCheckingAccount(atmTransactionDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deposit was successful", response.getBody());
    }

    @Test
    public void testDepositToCheckingAccount_IllegalArgumentException() {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(500.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("igmas@gmail.com");

        doThrow(new IllegalArgumentException("Invalid amount")).when(accountService).depositToCheckingAccount("igmas@gmail.com", 500.0);

        ResponseEntity<?> response = atmController.depositToCheckingAccount(atmTransactionDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid amount", response.getBody());
    }

    @Test
    public void testDepositToCheckingAccount_Exception() {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(500.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("igmas@gmail.com");

        doThrow(new RuntimeException("Unexpected error")).when(accountService).depositToCheckingAccount("igmas@gmail.com", 500.0);

        ResponseEntity<?> response = atmController.depositToCheckingAccount(atmTransactionDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred fetching balance", response.getBody());
    }

    @Test
    public void testWithdrawFromCheckingAccount_Success() {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(200.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("igmas@gmail.com");

        doNothing().when(accountService).withdrawFromCheckingAccount("igmas@gmail.com", 200.0);

        ResponseEntity<?> response = atmController.withdrawFromCheckingAccount(atmTransactionDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Withdraw was successful", response.getBody());
    }

    @Test
    public void testWithdrawFromCheckingAccount_IllegalArgumentException() {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(200.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("igmas@gmail.com");

        doThrow(new IllegalArgumentException("Insufficient funds")).when(accountService).withdrawFromCheckingAccount("igmas@gmail.com", 200.0);

        ResponseEntity<?> response = atmController.withdrawFromCheckingAccount(atmTransactionDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient funds", response.getBody());
    }

    @Test
    public void testWithdrawFromCheckingAccount_Exception() {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(200.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("igmas@gmail.com");

        doThrow(new RuntimeException("Unexpected error")).when(accountService).withdrawFromCheckingAccount("igmas@gmail.com", 200.0);

        ResponseEntity<?> response = atmController.withdrawFromCheckingAccount(atmTransactionDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred fetching balance", response.getBody());
    }
}
