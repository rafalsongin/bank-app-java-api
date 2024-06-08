package com.inholland.bankapp.unit_testing.controller;


import com.inholland.bankapp.controller.TransactionController;
import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.service.TransactionService;
import com.inholland.bankapp.unit_testing.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
@Import(TestSecurityConfig.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    // <editor-fold desc="Test for createTransaction">
    @Test
    @WithMockUser
    void createTransaction_ReturnsCreated() throws Exception {
        TransactionDto transactionDto = new TransactionDto();
        // Set fields on transactionDto as necessary
        when(transactionService.saveTransaction(any(TransactionDto.class))).thenReturn(transactionDto);

        this.mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(transactionDto)));
    }

    @Test
    @WithMockUser
    void createTransaction_ReturnsBadRequest() throws Exception {
        TransactionDto transactionDto = new TransactionDto();
        // Set fields on transactionDto as necessary
        when(transactionService.saveTransaction(any(TransactionDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid transaction data"));

        this.mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid transaction data"));
    }

    @Test
    @WithMockUser
    void createTransaction_ReturnsInternalServerError() throws Exception {
        TransactionDto transactionDto = new TransactionDto();
        // Set fields on transactionDto as necessary
        when(transactionService.saveTransaction(any(TransactionDto.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        this.mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    // </editor-fold>


}
