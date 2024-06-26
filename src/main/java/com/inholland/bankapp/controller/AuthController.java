package com.inholland.bankapp.controller;


import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.dto.EmployeeRegistrationDto;
import com.inholland.bankapp.dto.LoginDto;
import com.inholland.bankapp.exceptions.CustomerNotFoundException;
import com.inholland.bankapp.exceptions.InvalidDataException;
import com.inholland.bankapp.exceptions.UserAlreadyExistsException;
import com.inholland.bankapp.security.JwtTokenUtil;
import com.inholland.bankapp.service.CustomerService;
import com.inholland.bankapp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationDto registrationDto) {
        try {
            customerService.registerNewCustomer(registrationDto);
            return ResponseEntity.status(HttpStatus.OK).body("Customer registration form submitted successfully"); // returning 200 OK
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // returning 409 Conflict
        } catch (InvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // returning 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred"); // returning 500 Internal Server Error
        }
    }

    // This method is for back-end use only, not connected with the front-end
    @PostMapping("/register-employee")
    public ResponseEntity<?> registerEmployee(@RequestBody EmployeeRegistrationDto registrationDto) {
        try {
            employeeService.registerNewEmployee(registrationDto);
            return ResponseEntity.ok("Employee registered successfully");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // returning 409 Conflict
        } catch (InvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data provided"); // returning 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred"); // returning 500 Internal Server Error
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        return authenticateUserMethod(loginDto);
    }

    private ResponseEntity<String> authenticateUserMethod(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenUtil.generateToken(authentication);
            return ResponseEntity.ok(jwt);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/login-atm") // only for customers
    public ResponseEntity<?> authenticateCustomer(@RequestBody LoginDto loginDto) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

            customerService.getCustomerByEmail(loginDto.getUsername());
                    
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenUtil.generateToken(authentication);
            return ResponseEntity.ok(jwt);
        } catch (CustomerNotFoundException customerNFEx) {
            customerNFEx.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found!");
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not a customer");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
