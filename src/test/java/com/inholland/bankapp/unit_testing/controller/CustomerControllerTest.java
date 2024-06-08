package com.inholland.bankapp.unit_testing.controller;

import com.inholland.bankapp.controller.CustomerController;
import com.inholland.bankapp.dto.CustomerDto;
import com.inholland.bankapp.model.Bank;
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
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    // <editor-fold desc="Test dummy variables">

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String IBAN = "NL91ABNA0417164300";

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

}