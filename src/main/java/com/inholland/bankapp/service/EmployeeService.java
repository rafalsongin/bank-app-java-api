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
public class EmployeeService extends UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findEmployeeById(int id) {
        return employeeRepository.findById(id);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(int id) {
        employeeRepository.deleteById(id);
    }

    public void registerNewEmployee(EmployeeRegistrationDto registrationDto) {

        if (userExists(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Customer with " + registrationDto.getEmail() + " email already exists.");
        }
        
        Employee user = createEmployee(registrationDto);
        employeeRepository.save(user);
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
}