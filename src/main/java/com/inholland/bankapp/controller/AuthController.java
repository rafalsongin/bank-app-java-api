package com.inholland.bankapp.controller;


import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.dto.EmployeeRegistrationDto;
import com.inholland.bankapp.dto.LoginDto;
import com.inholland.bankapp.model.Customer;
import com.inholland.bankapp.model.Employee;
import com.inholland.bankapp.security.JwtTokenUtil;
import com.inholland.bankapp.service.CustomerService;
import com.inholland.bankapp.service.EmployeeService;
import com.inholland.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
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
        Customer customer = customerService.registerNewCustomer(registrationDto);
        return ResponseEntity.ok("Customer registered successfully");
    }
    
    @PostMapping("/register-employee")
    public ResponseEntity<?> registerEmployee(@RequestBody EmployeeRegistrationDto registrationDto) {
        Employee employee = employeeService.registerNewEmployee(registrationDto);
        return ResponseEntity.ok("Employee registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Testing message");
    }
}