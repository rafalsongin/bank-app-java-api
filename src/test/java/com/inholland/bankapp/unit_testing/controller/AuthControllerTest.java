/*
package com.inholland.bankapp.unit_testing.controller;

import com.inholland.bankapp.controller.AuthController;
import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.dto.EmployeeRegistrationDto;
import com.inholland.bankapp.dto.LoginDto;
import com.inholland.bankapp.exceptions.UserAlreadyExistsException;
import com.inholland.bankapp.exceptions.InvalidDataException;
import com.inholland.bankapp.security.JwtTokenUtil;
import com.inholland.bankapp.service.CustomerService;
import com.inholland.bankapp.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private CustomerService customerService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterCustomer_Success() throws Exception {
        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
        doNothing().when(customerService).registerNewCustomer(any(CustomerRegistrationDto.class));

        ResponseEntity<?> response = authController.registerCustomer(registrationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer registration form submitted successfully", response.getBody());
    }

    @Test
    void testRegisterCustomer_UserAlreadyExists() throws Exception {
        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
        doThrow(new UserAlreadyExistsException("User already exists")).when(customerService).registerNewCustomer(any(CustomerRegistrationDto.class));

        ResponseEntity<?> response = authController.registerCustomer(registrationDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    void testRegisterCustomer_InvalidData() throws Exception {
        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
        doThrow(new InvalidDataException("Invalid data")).when(customerService).registerNewCustomer(any(CustomerRegistrationDto.class));

        ResponseEntity<?> response = authController.registerCustomer(registrationDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data", response.getBody());
    }

    @Test
    void testRegisterCustomer_InternalServerError() throws Exception {
        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
        doThrow(new RuntimeException("Unexpected error")).when(customerService).registerNewCustomer(any(CustomerRegistrationDto.class));

        ResponseEntity<?> response = authController.registerCustomer(registrationDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", response.getBody());
    }

    @Test
    void testRegisterEmployee_Success() throws Exception {
        EmployeeRegistrationDto registrationDto = new EmployeeRegistrationDto();
        doNothing().when(employeeService).registerNewEmployee(any(EmployeeRegistrationDto.class));

        ResponseEntity<?> response = authController.registerEmployee(registrationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee registered successfully", response.getBody());
    }

    @Test
    void testRegisterEmployee_UserAlreadyExists() throws Exception {
        EmployeeRegistrationDto registrationDto = new EmployeeRegistrationDto();
        doThrow(new UserAlreadyExistsException("User already exists")).when(employeeService).registerNewEmployee(any(EmployeeRegistrationDto.class));

        ResponseEntity<?> response = authController.registerEmployee(registrationDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    void testRegisterEmployee_InvalidData() throws Exception {
        EmployeeRegistrationDto registrationDto = new EmployeeRegistrationDto();
        doThrow(new InvalidDataException("Invalid data")).when(employeeService).registerNewEmployee(any(EmployeeRegistrationDto.class));

        ResponseEntity<?> response = authController.registerEmployee(registrationDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data provided", response.getBody());
    }

    @Test
    void testRegisterEmployee_InternalServerError() throws Exception {
        EmployeeRegistrationDto registrationDto = new EmployeeRegistrationDto();
        doThrow(new RuntimeException("Unexpected error")).when(employeeService).registerNewEmployee(any(EmployeeRegistrationDto.class));

        ResponseEntity<?> response = authController.registerEmployee(registrationDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", response.getBody());
    }

    @Test
    void testAuthenticateUser_Success() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user");
        loginDto.setPassword("pass");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(authentication)).thenReturn("token");

        ResponseEntity<?> response = authController.authenticateUser(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody());
    }

    @Test
    void testAuthenticateUser_BadCredentials() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user");
        loginDto.setPassword("pass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.authenticateUser(loginDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }

    @Test
    void testAuthenticateUser_InternalServerError() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user");
        loginDto.setPassword("pass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = authController.authenticateUser(loginDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", response.getBody());
    }

    @Test
    void testAuthenticateCustomer_Success() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("customer");
        loginDto.setPassword("pass");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(authentication)).thenReturn("token");
        when(customerService.getCustomerByEmail(any(String.class))).thenReturn(Optional.of(new CustomerRegistrationDto()));

        ResponseEntity<?> response = authController.authenticateCustomer(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody());
    }

    @Test
    void testAuthenticateCustomer_NotCustomer() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("customer");
        loginDto.setPassword("pass");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(customerService.getCustomerByEmail(any(String.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.authenticateCustomer(loginDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User is not a customer", response.getBody());
    }

    @Test
    void testAuthenticateCustomer_BadCredentials() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("customer");
        loginDto.setPassword("pass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.authenticateCustomer(loginDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }

    @Test
    void testAuthenticateCustomer_InternalServerError() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("customer");
        loginDto.setPassword("pass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = authController.authenticateCustomer(loginDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", response.getBody());
    }
}
*/
