package com.inholland.bankapp.unit_testing.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.inholland.bankapp.controller.AccountController;
import com.inholland.bankapp.dto.AccountDto;
import com.inholland.bankapp.service.AccountService;
import com.inholland.bankapp.unit_testing.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;



import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
@Import(TestSecurityConfig.class)
class AccountControllerTestSpringMockito {

    // use mockMvc to simulate HTTP requests to a controller class
    @Autowired
    private MockMvc mockMvc;
   @MockBean
    private AccountService accountService;
    @Autowired
    private ObjectMapper objectMapper;

    // <editor-fold desc="Test for updateAccount">

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateAccount_ReturnsOk() throws Exception {
        String accountIBAN = "NL91ABNA0417164300";
        AccountDto updatedAccount = new AccountDto();
        // Set fields on updatedAccount as necessary
        when(accountService.updateAccount(eq(accountIBAN), any(AccountDto.class))).thenReturn(updatedAccount);

        this.mockMvc.perform(put("/api/accounts/{accountIBAN}", accountIBAN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAccount)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedAccount)));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateAccount_ReturnsBadRequest() throws Exception {
        String accountIBAN = "NL91ABNA0417164300";
        AccountDto updatedAccount = new AccountDto();
        when(accountService.updateAccount(eq(accountIBAN), any(AccountDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid account data"));

        this.mockMvc.perform(put("/api/accounts/{accountIBAN}", accountIBAN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAccount)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid account data"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateAccount_ReturnsInternalServerError() throws Exception {
        String accountIBAN = "NL91ABNA0417164300";
        AccountDto updatedAccount = new AccountDto();
        when(accountService.updateAccount(eq(accountIBAN), any(AccountDto.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        this.mockMvc.perform(put("/api/accounts/{accountIBAN}", accountIBAN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAccount)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    // </editor-fold>

    // <editor-fold desc="Test for getCheckingAccountByIBAN">

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCheckingAccountByIBAN_ReturnsOk() throws Exception {
        String IBAN = "NL91ABNA0417164300";
        AccountDto account = new AccountDto();
        // Set fields on account as necessary
        when(accountService.getCheckingAccountByIBAN(IBAN)).thenReturn(account);

        this.mockMvc.perform(get("/api/accounts/checking/{IBAN}", IBAN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(account)));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCheckingAccountByIBAN_ReturnsNotFound() throws Exception {
        String IBAN = "NL91ABNA0417164300";
        when(accountService.getCheckingAccountByIBAN(IBAN)).thenReturn(null);

        this.mockMvc.perform(get("/api/accounts/checking/{IBAN}", IBAN))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCheckingAccountByIBAN_ReturnsBadRequest() throws Exception {
        String IBAN = "NL91ABNA0417164300";
        when(accountService.getCheckingAccountByIBAN(IBAN))
                .thenThrow(new IllegalArgumentException("Invalid IBAN"));

        this.mockMvc.perform(get("/api/accounts/checking/{IBAN}", IBAN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid IBAN"));
    }

    // </editor-fold>

    // <editor-fold desc="Test for getAccountsByCustomerId">

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAccountsByCustomerId_ReturnsOk() throws Exception {
        Integer customerId = 1;
        List<AccountDto> accounts = Arrays.asList(new AccountDto(), new AccountDto());
        // Set fields on accounts as necessary
        when(accountService.getAccountsByCustomerId(customerId)).thenReturn(accounts);

        this.mockMvc.perform(get("/api/accounts/customer/{customerId}", customerId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAccountsByCustomerId_ReturnsNoContent() throws Exception {
        Integer customerId = 1;
        when(accountService.getAccountsByCustomerId(customerId)).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/api/accounts/customer/{customerId}", customerId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAccountsByCustomerId_ReturnsInternalServerError() throws Exception {
        Integer customerId = 1;
        when(accountService.getAccountsByCustomerId(customerId)).thenThrow(new RuntimeException("Internal server error"));

        this.mockMvc.perform(get("/api/accounts/customer/{customerId}", customerId))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    // </editor-fold>
    
}