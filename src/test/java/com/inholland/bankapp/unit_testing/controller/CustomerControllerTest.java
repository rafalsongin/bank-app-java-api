package com.inholland.bankapp.unit_testing.controller;

import com.inholland.bankapp.controller.CustomerController;
import com.inholland.bankapp.dto.CustomerDto;
import com.inholland.bankapp.model.UserRole;
import com.inholland.bankapp.service.CustomerService;
import com.inholland.bankapp.unit_testing.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
@Import(TestSecurityConfig.class)
class CustomerControllerTest {

    // <editor-fold desc="Test dummy variables">

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String IBAN = "NL91ABNA0417164300";
    private static final int CUSTOMER_ID = 30;

    // </editor-fold>


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;


    // <editor-fold desc="Test for get all customers">

    @Test
    @WithMockUser // Cezar
    void getAllCustomers_ReturnEmpty() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/api/customers"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser // Cezar
    void getAllCustomers() throws Exception
    {
        List<CustomerDto> customers = createCustomers();
        // Mock the service layer
        when(customerService.getAllCustomers()).thenReturn(customers);

        this.mockMvc.perform(get("/api/customers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].username", is("john_doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[1].userId", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Jane")))
                .andExpect(jsonPath("$[1].lastName", is("Doe")))
                .andExpect(jsonPath("$[1].username", is("jane_doe")))
                .andExpect(jsonPath("$[1].email", is("jane.doe@example.com")));

    }

    // </editor-fold>

    // <editor-fold desc="Test for getting IBAN by customer name">
    @Test
    @WithMockUser // Cezar
    void getIbanByCustomerName_ReturnsIban() throws Exception {
        when(customerService.getIbanByCustomerName(FIRST_NAME,LAST_NAME)).thenReturn(IBAN);

        this.mockMvc.perform(get("/api/customers/iban/{firstName}/{lastName}", FIRST_NAME,LAST_NAME))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(is(IBAN)));
    }

    @Test
    @WithMockUser // Cezar
    void getIbanByCustomerName_ReturnsNoContent() throws Exception {

        when(customerService.getIbanByCustomerName(FIRST_NAME,LAST_NAME)).thenReturn(null);

        this.mockMvc.perform(get("/api/customers/iban/{firstName}/{lastName}", FIRST_NAME,LAST_NAME))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser // Cezar
    void getIbanByCustomerName_ReturnsNotFound() throws Exception {

        when(customerService.getIbanByCustomerName(FIRST_NAME,LAST_NAME)).thenReturn("");

        this.mockMvc.perform(get("/api/customers/iban/{firstName}/{lastName}", FIRST_NAME,LAST_NAME))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    // </editor-fold>

    // <editor-fold desc="Test for approve customer">
    @Test
    @WithMockUser // Cezar
    void approveCustomer_ReturnsOk() throws Exception {
        doNothing().when(customerService).approveCustomer(CUSTOMER_ID);

        this.mockMvc.perform(post("/api/customers/approve/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Customer approved"));
    }

    @Test
    @WithMockUser // Cezar
    void approveCustomer_ReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid customer ID")).when(customerService).approveCustomer(CUSTOMER_ID);

        this.mockMvc.perform(post("/api/customers/approve/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid customer ID"));
    }

    @Test
    @WithMockUser // Cezar
    void approveCustomer_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("Internal server error")).when(customerService).approveCustomer(CUSTOMER_ID);

        this.mockMvc.perform(post("/api/customers/approve/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    // </editor-fold>

    // <editor-fold desc="Test for decline customer">

    @Test
    @WithMockUser
    void declineCustomer_ReturnsOk() throws Exception {
        doNothing().when(customerService).declineCustomer(CUSTOMER_ID);

        this.mockMvc.perform(post("/api/customers/decline/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Customer declined"));
    }

    @Test
    @WithMockUser
    void declineCustomer_ReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid customer ID")).when(customerService).declineCustomer(CUSTOMER_ID);

        this.mockMvc.perform(post("/api/customers/decline/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid customer ID"));
    }

    @Test
    @WithMockUser
    void declineCustomer_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("Internal server error")).when(customerService).declineCustomer(CUSTOMER_ID);

        this.mockMvc.perform(post("/api/customers/decline/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    // </editor-fold>

    private List<CustomerDto> createCustomers() {
        // Prepare test data
        CustomerDto customer1 = new CustomerDto();
        customer1.setUserId(1);
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setUsername("john_doe");
        customer1.setEmail("john.doe@example.com");
        customer1.setUserRole(UserRole.CUSTOMER); // Assuming you have a UserRole enum with CUSTOMER
        customer1.setPhoneNumber("123-456-7890");

        CustomerDto customer2 = new CustomerDto();
        customer2.setUserId(2);
        customer2.setFirstName("Jane");
        customer2.setLastName("Doe");
        customer2.setUsername("jane_doe");
        customer2.setEmail("jane.doe@example.com");
        customer2.setUserRole(UserRole.CUSTOMER);
        customer2.setPhoneNumber("098-765-4321");

        return Arrays.asList(customer1, customer2);
    }

    // <editor-fold desc="Test for closing customer account">
    @Test
    @WithMockUser // Mariia
    void closeCustomerAccount_ReturnsOk() throws Exception {
        doNothing().when(customerService).closeCustomerAccount(CUSTOMER_ID);

        this.mockMvc.perform(put("/api/customers/close/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Customer account closed"));
    }

    @Test
    @WithMockUser // Mariia
    void closeCustomerAccount_ReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid customer ID")).when(customerService).closeCustomerAccount(CUSTOMER_ID);

        this.mockMvc.perform(put("/api/customers/close/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid customer ID"));
    }

    @Test
    @WithMockUser // Mariia
    void closeCustomerAccount_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("Internal server error")).when(customerService).closeCustomerAccount(CUSTOMER_ID);

        this.mockMvc.perform(put("/api/customers/close/{customerID}", CUSTOMER_ID))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }
    // </editor-fold>
}