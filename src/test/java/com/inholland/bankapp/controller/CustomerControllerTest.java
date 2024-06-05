package com.inholland.bankapp.controller;

import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.model.UserRole;
import com.inholland.bankapp.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    @WithMockUser
    void getAllCustomers_ReturnEmpty() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/api/customers"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getAllCustomers() throws Exception {
        // Prepare test data
        Customer customer1 = new Customer();
        customer1.setUserId(1);
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setUsername("john_doe");
        customer1.setEmail("john.doe@example.com");
        customer1.setPassword("password");
        customer1.setBankId(1);
        customer1.setUserRole(UserRole.CUSTOMER); // Assuming you have a UserRole enum with CUSTOMER
        customer1.setBSN("123456789");
        customer1.setPhoneNumber("123-456-7890");
        customer1.setTransactionLimit(1000.0f);

        Customer customer2 = new Customer();
        customer2.setUserId(2);
        customer2.setFirstName("Jane");
        customer2.setLastName("Doe");
        customer2.setUsername("jane_doe");
        customer2.setEmail("jane.doe@example.com");
        customer2.setPassword("password");
        customer2.setBankId(1);
        customer2.setUserRole(UserRole.CUSTOMER);
        customer2.setBSN("987654321");
        customer2.setPhoneNumber("098-765-4321");
        customer2.setTransactionLimit(2000.0f);

        List<Customer> customers = Arrays.asList(customer1, customer2);

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

    @Test
    void getCustomerById() {
    }

    @Test
    void testGetCustomerById() {
    }

    @Test
    void getUnverifiedCustomers() {
    }

    @Test
    void approveCustomer() {
    }

    @Test
    void declineCustomer() {
    }

    @Test
    void closeCustomerAccount() {
    }

    @Test
    void updateCustomerDetails() {
    }
}