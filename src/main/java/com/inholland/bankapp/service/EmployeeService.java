package com.inholland.bankapp.service;

import com.inholland.bankapp.model.Employee;
import com.inholland.bankapp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

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
    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    // A method to save or update an employee
    public Employee saveOrUpdateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // A method to delete an employee by ID
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // Additional methods to handle other business logic related to employees
    // can be added here as well
}