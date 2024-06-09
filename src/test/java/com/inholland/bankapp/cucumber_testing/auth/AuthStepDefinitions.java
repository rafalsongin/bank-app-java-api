package com.inholland.bankapp.cucumber_testing.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inholland.bankapp.cucumber_testing.CommonStepDefinitions;
import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.dto.LoginDto;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class AuthStepDefinitions {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommonStepDefinitions commonStepDefinitions;

    private HttpHeaders headers = new HttpHeaders();
    private String baseUrl = "http://localhost:" + 8080 + "/auth";

    @Given("The following customer registration details")
    public void theFollowingCustomerRegistrationDetails(DataTable dataTable) {
        Map<String, String> customerData = dataTable.asMaps(String.class, String.class).get(0);
        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
        registrationDto.setEmail(customerData.get("email"));
        registrationDto.setPassword(customerData.get("password"));
        registrationDto.setFirstName(customerData.get("firstName"));
        registrationDto.setLastName(customerData.get("lastName"));
        registrationDto.setBsn(customerData.get("bsn"));
        registrationDto.setPhoneNumber(customerData.get("phoneNumber"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRegistrationDto> request = new HttpEntity<>(registrationDto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/register", request, String.class);
        commonStepDefinitions.setResponse(response);
    }

    @Given("The following login details")
    public void theFollowingLoginDetails(DataTable dataTable) {
        Map<String, String> loginData = dataTable.asMaps(String.class, String.class).get(0);
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(loginData.get("email"));
        loginDto.setPassword(loginData.get("password"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> request = new HttpEntity<>(loginDto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", request, String.class);
        commonStepDefinitions.setResponse(response);
    }

    @When("The customer submits the registration form")
    public void theCustomerSubmitsTheRegistrationForm() {
        // Submission is handled in the given step
    }

    @When("The user submits the login form")
    public void theUserSubmitsTheLoginForm() {
        // Submission is handled in the given step
    }

    @When("The customer submits the ATM login form")
    public void theCustomerSubmitsTheATMLoginForm(DataTable dataTable) {
        Map<String, String> loginData = dataTable.asMaps(String.class, String.class).get(0);
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(loginData.get("email"));
        loginDto.setPassword(loginData.get("password"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> request = new HttpEntity<>(loginDto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login-atm", request, String.class);
        commonStepDefinitions.setResponse(response);
    }
}
