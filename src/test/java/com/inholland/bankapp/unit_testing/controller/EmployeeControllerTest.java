package com.inholland.bankapp.unit_testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inholland.bankapp.controller.EmployeeController;
import com.inholland.bankapp.model.Employee;
import com.inholland.bankapp.service.EmployeeService;
import com.inholland.bankapp.unit_testing.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
@Import(TestSecurityConfig.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    // <editor-fold desc="Test for getEmployeeByEmail">
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getEmployeeByEmail_ReturnsOk() throws Exception {
        Employee employee = new Employee();
        employee.setEmail("test@example.com");
        employee.setUsername("TestName");
        // Set other necessary fields

        when(employeeService.getEmployeeByEmail(anyString())).thenReturn(Optional.of(employee));

        this.mockMvc.perform(get("/api/employees/email/{email}", "test@example.com"))
                .andDo(print())
                    .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.username", is("TestName")));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getEmployeeByEmail_ReturnsNotFound() throws Exception {
        when(employeeService.getEmployeeByEmail(anyString())).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/api/employees/email/{email}", "test@example.com"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getEmployeeByEmail_ReturnsInternalServerError() throws Exception {
        when(employeeService.getEmployeeByEmail(anyString())).thenThrow(new RuntimeException("Internal server error"));

        this.mockMvc.perform(get("/api/employees/email/{email}", "test@example.com"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    // </editor-fold>

}
