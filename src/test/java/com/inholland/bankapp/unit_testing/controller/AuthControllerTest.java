package com.inholland.bankapp.unit_testing.controller;

import com.inholland.bankapp.controller.AuthController;
import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.dto.EmployeeRegistrationDto;
import com.inholland.bankapp.dto.LoginDto;
import com.inholland.bankapp.exceptions.UserAlreadyExistsException;
import com.inholland.bankapp.exceptions.InvalidDataException;
import com.inholland.bankapp.model.Customer;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
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

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterCustomer_Success() {
        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
        doNothing().when(customerService).registerNewCustomer(registrationDto);

        ResponseEntity<?> response = authController.registerCustomer(registrationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer registration form submitted successfully", response.getBody());
    }

    @Test
    public void testRegisterCustomer_UserAlreadyExists() {
        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
        doThrow(new UserAlreadyExistsException("User already exists")).when(customerService).registerNewCustomer(registrationDto);

        ResponseEntity<?> response = authController.registerCustomer(registrationDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    public void testRegisterCustomer_InvalidData() {
        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
        doThrow(new InvalidDataException("Invalid data")).when(customerService).registerNewCustomer(registrationDto);

        ResponseEntity<?> response = authController.registerCustomer(registrationDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data", response.getBody());
    }

    @Test
    public void testRegisterEmployee_Success() {
        EmployeeRegistrationDto registrationDto = new EmployeeRegistrationDto();
        doNothing().when(employeeService).registerNewEmployee(registrationDto);

        ResponseEntity<?> response = authController.registerEmployee(registrationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee registered successfully", response.getBody());
    }

    @Test
    public void testRegisterEmployee_UserAlreadyExists() {
        EmployeeRegistrationDto registrationDto = new EmployeeRegistrationDto();
        doThrow(new UserAlreadyExistsException("User already exists")).when(employeeService).registerNewEmployee(registrationDto);

        ResponseEntity<?> response = authController.registerEmployee(registrationDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    public void testRegisterEmployee_InvalidData() {
        EmployeeRegistrationDto registrationDto = new EmployeeRegistrationDto();
        doThrow(new InvalidDataException("Invalid data")).when(employeeService).registerNewEmployee(registrationDto);

        ResponseEntity<?> response = authController.registerEmployee(registrationDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data provided", response.getBody());
    }

    @Test
    public void testAuthenticateUser_Success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user@example.com");
        loginDto.setPassword("correct-password");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenUtil.generateToken(authentication)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.authenticateUser(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
    }

    @Test
    public void testAuthenticateUser_BadCredentials() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user@example.com");
        loginDto.setPassword("wrong-password");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.authenticateUser(loginDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }

    @Test
    public void testAuthenticateCustomer_Success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user@example.com");
        loginDto.setPassword("correct-password");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(customerService.getCustomerByEmail(loginDto.getUsername())).thenReturn(Optional.of(new Customer()));
        when(jwtTokenUtil.generateToken(authentication)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.authenticateCustomer(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
    }

    @Test
    public void testAuthenticateCustomer_NotACustomer() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user@example.com");
        loginDto.setPassword("correct-password");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(customerService.getCustomerByEmail(loginDto.getUsername())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.authenticateCustomer(loginDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User is not a customer", response.getBody());
    }

    @Test
    public void testAuthenticateCustomer_BadCredentials() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user@example.com");
        loginDto.setPassword("wrong-password");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.authenticateCustomer(loginDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }
}
