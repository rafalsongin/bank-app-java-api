package com.inholland.bankapp.service;

import com.inholland.bankapp.dto.EmployeeRegistrationDto;
import com.inholland.bankapp.exceptions.UserAlreadyExistsException;
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

    public Employee registerNewEmployee(EmployeeRegistrationDto registrationDto) {

        if (employeeExists(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Customer with " + registrationDto.getEmail() + " email already exists.");
        }
        
        Employee user = createEmployee(registrationDto);
        return employeeRepository.save(user);
    }
    
    private Employee createEmployee(EmployeeRegistrationDto registrationDto) {
        Employee user = new Employee();

        String username = registrationDto.getFirstName() + registrationDto.getLastName();
        user.setUsername(username);
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setBankId(1);
        user.setUserRole(UserRole.EMPLOYEE);
        user.setEmploymentStatus(EmploymentStatus.ACTIVE);
        
        return user;
    }

    private boolean employeeExists(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }
}