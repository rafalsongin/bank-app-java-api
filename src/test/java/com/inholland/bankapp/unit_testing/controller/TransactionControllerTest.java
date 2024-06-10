package com.inholland.bankapp.unit_testing.controller;


import com.inholland.bankapp.controller.TransactionController;
import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.exceptions.AccountNotFoundException;
import com.inholland.bankapp.service.TransactionService;
import com.inholland.bankapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @MockBean
    private UserService userService;

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


    // <editor-fold desc="Test for getAllTransactions">
    @Test
    @WithMockUser
    void testGetAllTransactions_ReturnsOk() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactions(anyInt(), anyInt(), any(), any(), any(), any(), any(), any())).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void testGetAllTransactions_UserDoesNotExist() throws Exception {
        String username = "nonexistentuser";
        String role = "EMPLOYEE";

        when(userService.userExists(username)).thenReturn(false);

        mockMvc.perform(get("/api/transactions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized access"));
    }

    @Test
    public void testGetAllTransactions_UnauthorizedAccess() throws Exception {
        String username = "testuser";
        String role = "CUSTOMER";

        when(userService.userExists(username)).thenReturn(true);

        mockMvc.perform(get("/api/transactions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized access"));
    }

    @Test
    public void testGetAllTransactions_UnauthorizedAccessUserDoesNotExist() throws Exception {
        String username = "testuser";
        String role = "CUSTOMER";

        when(userService.userExists(username)).thenReturn(false);

        mockMvc.perform(get("/api/transactions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized access"));
    }

    @Test
    public void testGetAllTransactions_InvalidUserRole() throws Exception {
        String username = "testuser";
        String invalidRole = "INVALID_ROLE";

        when(userService.userExists(username)).thenReturn(true);

        mockMvc.perform(get("/api/transactions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", invalidRole))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid role"));
    }

    @Test
    @WithMockUser
    void testGetAllTransactions_ReturnsInternalServerError() throws Exception {
        when(transactionService.getAllTransactions(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Internal server error"));
        when(userService.userExists(anyString())).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", "test_user")
                        .param("role", "EMPLOYEE"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error occurred while fetching transactions. Please try again later."));
    }

    @Test
    public void testGetAllTransactions_AmountIsNegative() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        float amountValue = -2;

        when(userService.userExists(username)).thenReturn(true);
        doThrow(new IllegalArgumentException("Amount can't be negative, 0 or infinite"))
                .when(transactionService).getAllTransactions(page, size, null, null, "equals", amountValue, null, null);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("amountCondition", "equals")
                        .param("amountValue", Float.toString(amountValue))
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount can't be negative, 0 or infinite"));

        verify(transactionService).getAllTransactions(page, size, null, null, "equals", amountValue, null, null);
    }

    @Test
    public void testGetAllTransactions_InvalidIban() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String invalidIban = "INVALID_IBAN";

        when(userService.userExists(username)).thenReturn(true);
        doThrow(new IllegalArgumentException("Invalid IBAN format"))
                .when(transactionService).getAllTransactions(page, size, null, null, null, null, invalidIban, null);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("fromIban", invalidIban)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid IBAN format"));

        verify(transactionService).getAllTransactions(page, size, null, null, null, null, invalidIban, null);
    }

    @Test
    public void testGetAllTransactions_AccountNotFound() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String validIban = "NL12ABCD1234567890";

        when(userService.userExists(username)).thenReturn(true);
        doThrow(new AccountNotFoundException(validIban))
                .when(transactionService).getAllTransactions(page, size, null, null, null, null, validIban, null);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("fromIban", validIban)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account [NL12ABCD1234567890] not found!"));

        verify(transactionService).getAllTransactions(page, size, null, null, null, null, validIban, null);
    }

    @Test
    public void testGetAllTransactions_EndDateEarlierThanStartDate() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String startDate = "2023-12-31";
        String endDate = "2023-01-01";

        when(userService.userExists(username)).thenReturn(true);
        doThrow(new IllegalArgumentException("End date must be after start date"))
                .when(transactionService).getAllTransactions(page, size, LocalDate.parse(startDate), LocalDate.parse(endDate), null, null, null, null);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("End date must be after start date"));

        verify(transactionService).getAllTransactions(page, size, LocalDate.parse(startDate), LocalDate.parse(endDate), null, null, null, null);
    }

    @Test
    public void testGetAllTransactions_NoTransactionsFound() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;

        Page<TransactionDto> emptyTransactions = new PageImpl<>(Collections.emptyList());

        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(anyInt(), anyInt(), any(), any(), any(), any(), any(), any())).thenReturn(emptyTransactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    private static Page<TransactionDto> getTransactionDtos() {
        TransactionDto transactionDto1 = new TransactionDto();
        transactionDto1.setAmount(100.0f);
        transactionDto1.setFromAccount("NL91ABNA0417164300");
        transactionDto1.setToAccount("NL91ABNA0417164301");
        transactionDto1.setTransactionType("External Transaction");
        transactionDto1.setInitiatedByUser(1);
        transactionDto1.setInitiatorName("test_user");
        transactionDto1.setInitiatorRole("EMPLOYEE");
        transactionDto1.setTimestamp("2024-06-01T12:00:00");

        TransactionDto transactionDto2 = new TransactionDto();
        transactionDto2.setAmount(200.0f);
        transactionDto2.setFromAccount("NL91ABNA0417164302");
        transactionDto2.setToAccount("NL91ABNA0417164303");
        transactionDto2.setTransactionType("Internal Transaction");
        transactionDto2.setInitiatedByUser(2);
        transactionDto2.setInitiatorName("test_user");
        transactionDto2.setInitiatorRole("CUSTOMER");
        transactionDto2.setTimestamp("2024-06-02T12:00:00");

        List<TransactionDto> transactionDtos = Arrays.asList(transactionDto1, transactionDto2);

        Page<TransactionDto> transactions = new PageImpl<>(transactionDtos);
        return transactions;
    }

    @Test
    @WithMockUser
    void testGetAllTransactions_ReturnsOkWithoutFilters() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;

        Page<TransactionDto> transactions = getTransactionDtos();

        when(transactionService.getAllTransactions(eq(page), eq(size), any(), any(), any(), any(), any(), any())).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("username", username)
                        .param("role", role))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].amount", is(100.0)))
                .andExpect(jsonPath("$.content[0].fromAccount", is("NL91ABNA0417164300")))
                .andExpect(jsonPath("$.content[0].toAccount", is("NL91ABNA0417164301")))
                .andExpect(jsonPath("$.content[0].transactionType", is("External Transaction")))
                .andExpect(jsonPath("$.content[0].initiatedByUser", is(1)))
                .andExpect(jsonPath("$.content[0].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[0].initiatorRole", is("EMPLOYEE")))
                .andExpect(jsonPath("$.content[0].timestamp", is("2024-06-01T12:00:00")))

                .andExpect(jsonPath("$.content[1].amount", is(200.0)))
                .andExpect(jsonPath("$.content[1].fromAccount", is("NL91ABNA0417164302")))
                .andExpect(jsonPath("$.content[1].toAccount", is("NL91ABNA0417164303")))
                .andExpect(jsonPath("$.content[1].transactionType", is("Internal Transaction")))
                .andExpect(jsonPath("$.content[1].initiatedByUser", is(2)))
                .andExpect(jsonPath("$.content[1].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[1].initiatorRole", is("CUSTOMER")))
                .andExpect(jsonPath("$.content[1].timestamp", is("2024-06-02T12:00:00")));
    }

    @Test
    public void testGetAllTransactions_MatchingDateRange() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";

        Page<TransactionDto> transactions = getTransactionDtos();
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, LocalDate.parse(startDate), LocalDate.parse(endDate), null, null, null, null)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].amount", is(100.0)))
                .andExpect(jsonPath("$.content[0].fromAccount", is("NL91ABNA0417164300")))
                .andExpect(jsonPath("$.content[0].toAccount", is("NL91ABNA0417164301")))
                .andExpect(jsonPath("$.content[0].transactionType", is("External Transaction")))
                .andExpect(jsonPath("$.content[0].initiatedByUser", is(1)))
                .andExpect(jsonPath("$.content[0].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[0].initiatorRole", is("EMPLOYEE")))
                .andExpect(jsonPath("$.content[0].timestamp", is("2024-06-01T12:00:00")))
                .andExpect(jsonPath("$.content[1].amount", is(200.0)))
                .andExpect(jsonPath("$.content[1].fromAccount", is("NL91ABNA0417164302")))
                .andExpect(jsonPath("$.content[1].toAccount", is("NL91ABNA0417164303")))
                .andExpect(jsonPath("$.content[1].transactionType", is("Internal Transaction")))
                .andExpect(jsonPath("$.content[1].initiatedByUser", is(2)))
                .andExpect(jsonPath("$.content[1].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[1].initiatorRole", is("CUSTOMER")))
                .andExpect(jsonPath("$.content[1].timestamp", is("2024-06-02T12:00:00")));

        verify(transactionService).getAllTransactions(page, size, LocalDate.parse(startDate), LocalDate.parse(endDate), null, null, null, null);
    }

    @Test
    public void testGetAllTransactions_MatchingAmountCondition() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        float amountValue = 100.0f;
        String amountCondition = "greaterThan";

        Page<TransactionDto> transactions = getTransactionDtos();
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, null, null, amountCondition, amountValue, null, null)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("amountCondition", amountCondition)
                        .param("amountValue", Float.toString(amountValue))
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].amount", is(100.0)))
                .andExpect(jsonPath("$.content[0].fromAccount", is("NL91ABNA0417164300")))
                .andExpect(jsonPath("$.content[0].toAccount", is("NL91ABNA0417164301")))
                .andExpect(jsonPath("$.content[0].transactionType", is("External Transaction")))
                .andExpect(jsonPath("$.content[0].initiatedByUser", is(1)))
                .andExpect(jsonPath("$.content[0].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[0].initiatorRole", is("EMPLOYEE")))
                .andExpect(jsonPath("$.content[0].timestamp", is("2024-06-01T12:00:00")))
                .andExpect(jsonPath("$.content[1].amount", is(200.0)))
                .andExpect(jsonPath("$.content[1].fromAccount", is("NL91ABNA0417164302")))
                .andExpect(jsonPath("$.content[1].toAccount", is("NL91ABNA0417164303")))
                .andExpect(jsonPath("$.content[1].transactionType", is("Internal Transaction")))
                .andExpect(jsonPath("$.content[1].initiatedByUser", is(2)))
                .andExpect(jsonPath("$.content[1].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[1].initiatorRole", is("CUSTOMER")))
                .andExpect(jsonPath("$.content[1].timestamp", is("2024-06-02T12:00:00")));

        verify(transactionService).getAllTransactions(page, size, null, null, amountCondition, amountValue, null, null);
    }

    @Test
    public void testGetAllTransactions_MatchingFromIban() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String fromIban = "NL91ABNA0417164300";

        Page<TransactionDto> transactions = getTransactionDtos();
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, null, null, null, null, fromIban, null)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("fromIban", fromIban)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].amount", is(100.0)))
                .andExpect(jsonPath("$.content[0].fromAccount", is("NL91ABNA0417164300")))
                .andExpect(jsonPath("$.content[0].toAccount", is("NL91ABNA0417164301")))
                .andExpect(jsonPath("$.content[0].transactionType", is("External Transaction")))
                .andExpect(jsonPath("$.content[0].initiatedByUser", is(1)))
                .andExpect(jsonPath("$.content[0].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[0].initiatorRole", is("EMPLOYEE")))
                .andExpect(jsonPath("$.content[0].timestamp", is("2024-06-01T12:00:00")))
                .andExpect(jsonPath("$.content[1].amount", is(200.0)))
                .andExpect(jsonPath("$.content[1].fromAccount", is("NL91ABNA0417164302")))
                .andExpect(jsonPath("$.content[1].toAccount", is("NL91ABNA0417164303")))
                .andExpect(jsonPath("$.content[1].transactionType", is("Internal Transaction")))
                .andExpect(jsonPath("$.content[1].initiatedByUser", is(2)))
                .andExpect(jsonPath("$.content[1].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[1].initiatorRole", is("CUSTOMER")))
                .andExpect(jsonPath("$.content[1].timestamp", is("2024-06-02T12:00:00")));

        verify(transactionService).getAllTransactions(page, size, null, null, null, null, fromIban, null);
    }

    @Test
    public void testGetAllTransactions_MatchingToIban() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String toIban = "NL91ABNA0417164301";

        Page<TransactionDto> transactions = getTransactionDtos();
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, null, null, null, null, null, toIban)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("toIban", toIban)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].amount", is(100.0)))
                .andExpect(jsonPath("$.content[0].fromAccount", is("NL91ABNA0417164300")))
                .andExpect(jsonPath("$.content[0].toAccount", is("NL91ABNA0417164301")))
                .andExpect(jsonPath("$.content[0].transactionType", is("External Transaction")))
                .andExpect(jsonPath("$.content[0].initiatedByUser", is(1)))
                .andExpect(jsonPath("$.content[0].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[0].initiatorRole", is("EMPLOYEE")))
                .andExpect(jsonPath("$.content[0].timestamp", is("2024-06-01T12:00:00")))
                .andExpect(jsonPath("$.content[1].amount", is(200.0)))
                .andExpect(jsonPath("$.content[1].fromAccount", is("NL91ABNA0417164302")))
                .andExpect(jsonPath("$.content[1].toAccount", is("NL91ABNA0417164303")))
                .andExpect(jsonPath("$.content[1].transactionType", is("Internal Transaction")))
                .andExpect(jsonPath("$.content[1].initiatedByUser", is(2)))
                .andExpect(jsonPath("$.content[1].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[1].initiatorRole", is("CUSTOMER")))
                .andExpect(jsonPath("$.content[1].timestamp", is("2024-06-02T12:00:00")));

        verify(transactionService).getAllTransactions(page, size, null, null, null, null, null, toIban);
    }

    @Test
    public void testGetAllTransactions_MatchingMultipleFilters() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        String amountCondition = "greaterThan";
        float amountValue = 100.0f;
        String fromIban = "NL91ABNA0417164300";
        String toIban = "NL91ABNA0417164301";

        Page<TransactionDto> transactions = getTransactionDtos();
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, startDate, endDate, amountCondition, amountValue, fromIban, toIban)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("amountCondition", amountCondition)
                        .param("amountValue", Float.toString(amountValue))
                        .param("fromIban", fromIban)
                        .param("toIban", toIban)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].amount", is(100.0)))
                .andExpect(jsonPath("$.content[0].fromAccount", is("NL91ABNA0417164300")))
                .andExpect(jsonPath("$.content[0].toAccount", is("NL91ABNA0417164301")))
                .andExpect(jsonPath("$.content[0].transactionType", is("External Transaction")))
                .andExpect(jsonPath("$.content[0].initiatedByUser", is(1)))
                .andExpect(jsonPath("$.content[0].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[0].initiatorRole", is("EMPLOYEE")))
                .andExpect(jsonPath("$.content[0].timestamp", is("2024-06-01T12:00:00")))
                .andExpect(jsonPath("$.content[1].amount", is(200.0)))
                .andExpect(jsonPath("$.content[1].fromAccount", is("NL91ABNA0417164302")))
                .andExpect(jsonPath("$.content[1].toAccount", is("NL91ABNA0417164303")))
                .andExpect(jsonPath("$.content[1].transactionType", is("Internal Transaction")))
                .andExpect(jsonPath("$.content[1].initiatedByUser", is(2)))
                .andExpect(jsonPath("$.content[1].initiatorName", is("test_user")))
                .andExpect(jsonPath("$.content[1].initiatorRole", is("CUSTOMER")))
                .andExpect(jsonPath("$.content[1].timestamp", is("2024-06-02T12:00:00")));

        verify(transactionService).getAllTransactions(page, size, startDate, endDate, amountCondition, amountValue, fromIban, toIban);
    }

    @Test
    public void testGetAllTransactions_MatchingFromDate() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String startDate = "2024-01-01";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, LocalDate.parse(startDate), null, null, null, null, null)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("startDate", startDate)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(transactionService).getAllTransactions(page, size, LocalDate.parse(startDate), null, null, null, null, null);
    }

    @Test
    public void testGetAllTransactions_MatchingToDate() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String endDate = "2024-12-31";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, null, LocalDate.parse(endDate), null, null, null, null)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("endDate", endDate)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(transactionService).getAllTransactions(page, size, null, LocalDate.parse(endDate), null, null, null, null);
    }

    @Test
    public void testGetAllTransactions_MatchingFromIbanAndToIban() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        String fromIban = "NL91ABNA0417164300";
        String toIban = "NL91ABNA0417164301";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, null, null, null, null, fromIban, toIban)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("fromIban", fromIban)
                        .param("toIban", toIban)
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(transactionService).getAllTransactions(page, size, null, null, null, null, fromIban, toIban);
    }

    @Test
    public void testGetAllTransactions_AmountLessThan() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        float amountValue = 2000.0f;
        String amountCondition = "lessThan";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, null, null, amountCondition, amountValue, null, null)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("amountCondition", amountCondition)
                        .param("amountValue", Float.toString(amountValue))
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(transactionService).getAllTransactions(page, size, null, null, amountCondition, amountValue, null, null);
    }

    @Test
    public void testGetAllTransactions_AmountEqualTo() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        int page = 1;
        int size = 10;
        float amountValue = 150.0f;
        String amountCondition = "equals";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(userService.userExists(username)).thenReturn(true);
        when(transactionService.getAllTransactions(page, size, null, null, amountCondition, amountValue, null, null)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("amountCondition", amountCondition)
                        .param("amountValue", Float.toString(amountValue))
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(transactionService).getAllTransactions(page, size, null, null, amountCondition, amountValue, null, null);
    }
    // </editor-fold>

    // <editor-fold desc="Test for getAllTransactionsByIban">
    @Test
    @WithMockUser
    void testCustomerGetAllTransactionsByIban_ReturnsOk() throws Exception {
        String username = "user";
        String role = "CUSTOMER";
        String iban = "NL91ABNA0417164300";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), anyString(), any(), any(), any(), any(), any(), any())).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);
        when(userService.isAccountOwner(username, iban)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithMockUser
    void testEmployeeGetAllTransactionsByIban_ReturnsOk() throws Exception {
        String username = "employeeUser";
        String role = "EMPLOYEE";
        String iban = "NL91ABNA0417164300";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), anyString(), any(), any(), any(), any(), any(), any())).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithMockUser
    void testCustomerGetAllTransactionsByIban_ReturnsUnauthorized() throws Exception {
        String username = "user";
        String role = "CUSTOMER";
        String iban = "NL91ABNA0417164300";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), eq(iban), any(), any(), any(), any(), any(), any())).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);
        when(userService.isAccountOwner(username, iban)).thenReturn(false);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized access"));
    }

    @Test
    @WithMockUser
    void testGetAllTransactionsByIban_UserDoesNotExist() throws Exception {
        String username = "test_user";
        String role = "CUSTOMER";
        String iban = "NL91ABNA0417164300";

        when(userService.userExists(username)).thenReturn(false);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized access"));
    }

    @Test
    public void testGetAllTransactionsByIban_InvalidUserRole() throws Exception {
        String username = "test_user";
        String invalidRole = "INVALID_ROLE";
        String iban = "NL91ABNA0417164300";

        when(userService.userExists(username)).thenReturn(true);

        mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", invalidRole))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid role"));
    }

    @Test
    @WithMockUser
    void testGetAllTransactionsByIban_ReturnsBadRequest() throws Exception {
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), anyString(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid request parameters"));
        when(userService.userExists(anyString())).thenReturn(true);
        when(userService.isAccountOwner(anyString(), anyString())).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", "NL91ABNA0417164300")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", "test_user")
                        .param("role", "CUSTOMER"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request parameters"));
    }

    @Test
    @WithMockUser
    void testGetAllTransactionsByIban_ReturnsInternalServerError() throws Exception {
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), anyString(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Internal server error"));
        when(userService.userExists(anyString())).thenReturn(true);
        when(userService.isAccountOwner(anyString(), anyString())).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", "NL91ABNA0417164300")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", "test_user")
                        .param("role", "CUSTOMER"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser
    void testGetAllTransactionsByIban_WithDateRangeFilter_ReturnsOk() throws Exception {
        String username = "user";
        String role = "CUSTOMER";
        String iban = "NL91ABNA0417164300";
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), eq(iban), eq(LocalDate.parse(startDate)), eq(LocalDate.parse(endDate)), any(), any(), any(), any())).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);
        when(userService.isAccountOwner(username, iban)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role)
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithMockUser
    void testGetAllTransactionsByIban_WithAmountFilter_ReturnsOk() throws Exception {
        String username = "user";
        String role = "CUSTOMER";
        String iban = "NL91ABNA0417164300";
        String amountCondition = "greaterThan";
        String amountValue = "100.00";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), eq(iban), any(), any(), eq(amountCondition), eq(Float.parseFloat(amountValue)), any(), any())).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);
        when(userService.isAccountOwner(username, iban)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role)
                        .param("amountCondition", amountCondition)
                        .param("amountValue", amountValue))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithMockUser
    void testGetAllTransactionsByIban_WithFromIbanFilter_ReturnsOk() throws Exception {
        String username = "user";
        String role = "CUSTOMER";
        String iban = "NL91ABNA0417164300";
        String fromIban = "NL91ABNA0417164301";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), eq(iban), any(), any(), any(), any(), eq(fromIban), any())).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);
        when(userService.isAccountOwner(username, iban)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role)
                        .param("fromIban", fromIban))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithMockUser
    void testGetAllTransactionsByIban_WithToIbanFilter_ReturnsOk() throws Exception {
        String username = "user";
        String role = "CUSTOMER";
        String iban = "NL91ABNA0417164300";
        String toIban = "NL91ABNA0417164302";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), eq(iban), any(), any(), any(), any(), any(), eq(toIban))).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);
        when(userService.isAccountOwner(username, iban)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role)
                        .param("toIban", toIban))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithMockUser
    void testGetAllTransactionsByIban_WithAllFilters_ReturnsOk() throws Exception {
        String username = "user";
        String role = "CUSTOMER";
        String iban = "NL91ABNA0417164300";
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        String amountCondition = "greaterThan";
        String amountValue = "100.00";
        String fromIban = "NL91ABNA0417164301";
        String toIban = "NL91ABNA0417164302";

        Page<TransactionDto> transactions = new PageImpl<>(Collections.singletonList(new TransactionDto()));
        when(transactionService.getAllTransactionsByIban(anyInt(), anyInt(), eq(iban), eq(LocalDate.parse(startDate)), eq(LocalDate.parse(endDate)), eq(amountCondition), eq(Float.parseFloat(amountValue)), eq(fromIban), eq(toIban))).thenReturn(transactions);
        when(userService.userExists(username)).thenReturn(true);
        when(userService.isAccountOwner(username, iban)).thenReturn(true);

        this.mockMvc.perform(get("/api/transactions/account/{iban}", iban)
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", username)
                        .param("role", role)
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .param("amountCondition", amountCondition)
                        .param("amountValue", amountValue)
                        .param("fromIban", fromIban)
                        .param("toIban", toIban))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
    // </editor-fold>
}