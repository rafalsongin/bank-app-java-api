package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.dto.EmployeeRegistrationDto;
import com.inholland.bankapp.model.*;
import com.inholland.bankapp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final EmployeeRepository employeeRepository;

    // Constructor injection is recommended
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // A method to find all employees
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    // A method to find an employee by ID
    public Optional<Employee> findEmployeeById(int id) {
        return employeeRepository.findById(id);
    }

    // A method to save or update an employee
    public Employee saveOrUpdateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // A method to delete an employee by ID
    public void deleteEmployee(int id) {
        employeeRepository.deleteById(id);
    }

    // Additional methods to handle other business logic related to employees
    // can be added here as well


    public Employee registerNewEmployee(EmployeeRegistrationDto registrationDto) {
        Employee user = new Employee();

        // Set username to a combination of firstName and lastName. Ensure this is unique or handled appropriately.
        String username = registrationDto.getFirstName() + registrationDto.getLastName();

        user.setUsername(username);
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());

        // Assuming you are setting some default or fetching an existing bank_id
        user.setBankId(1); // You need to have a valid bank_id or retrieve it dynamically as per your logic
        
        user.setUserRole(UserRole.EMPLOYEE);

        // For Employee specific fields
        user.setEmploymentStatus(EmploymentStatus.ACTIVE);

        return employeeRepository.save(user);
    }
}