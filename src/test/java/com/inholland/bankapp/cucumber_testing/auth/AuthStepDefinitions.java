package com.inholland.bankapp.cucumber_testing.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inholland.bankapp.dto.CustomerRegistrationDto;
import com.inholland.bankapp.dto.LoginDto;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import com.inholland.bankapp.cucumber_testing.BaseStepDefinitions;

import java.util.List;
import java.util.Map;

public class AuthStepDefinitions extends BaseStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    private HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;
    private Exception exception;

    @Given("The following customer registration details")
    public void theFollowingCustomerRegistrationDetails(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        CustomerRegistrationDto registerDto = new CustomerRegistrationDto();
        registerDto.setEmail(data.get(0).get("email"));
        registerDto.setPassword(data.get(0).get("password"));
        registerDto.setFirstName(data.get(0).get("firstName"));
        registerDto.setLastName(data.get(0).get("lastName"));
        registerDto.setBsn(data.get(0).get("bsn"));
        registerDto.setPhoneNumber(data.get(0).get("phoneNumber"));

        try {
            String requestBody = mapper.writeValueAsString(registerDto);
            httpHeaders.add("Content-Type", "application/json");
            HttpEntity<String> request = new HttpEntity<>(requestBody, httpHeaders);
            response = restTemplate.exchange(
                    "/auth/register",
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            exception = e;
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("The customer submits the registration form")
    public void theCustomerSubmitsTheRegistrationForm() {
        // Registration is already submitted in @Given step
    }

    @Then("The response status code should be {int}")
    public void theResponseStatusShouldBe(int status) {
        if (exception != null) {
            if (exception instanceof HttpClientErrorException) {
                HttpClientErrorException clientErrorException = (HttpClientErrorException) exception;
                Assertions.assertEquals(status, clientErrorException.getStatusCode().value(), "Status code does not match");
            } else if (exception instanceof ResourceAccessException) {
                // Handle I/O error specifically
                Assertions.fail("Unexpected exception occurred: " + exception.getMessage());
            } else {
                Assertions.fail("Unexpected exception occurred: " + exception.getMessage());
            }
        } else {
            Assertions.assertNotNull(response, "Response is null");
            int actual = response.getStatusCode().value();
            Assertions.assertEquals(status, actual);
        }
    }

    @Given("The following login details")
    public void theFollowingLoginDetails(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(data.get(0).get("email"));
        loginDto.setPassword(data.get(0).get("password"));

        try {
            String requestBody = mapper.writeValueAsString(loginDto);
            httpHeaders.add("Content-Type", "application/json");
            HttpEntity<String> request = new HttpEntity<>(requestBody, httpHeaders);
            response = restTemplate.exchange(
                    "/auth/login",
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            exception = e;
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            exception = e;
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("The user submits the login form")
    public void theUserSubmitsTheLoginForm() {
        // Login is already submitted in @Given step
    }

    @Given("The following ATM login details")
    public void theFollowingATMLoginDetails(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(data.get(0).get("email"));
        loginDto.setPassword(data.get(0).get("password"));

        try {
            String requestBody = mapper.writeValueAsString(loginDto);
            httpHeaders.add("Content-Type", "application/json");
            HttpEntity<String> request = new HttpEntity<>(requestBody, httpHeaders);
            response = restTemplate.exchange(
                    "/auth/atm-login",
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            exception = e;
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("The customer submits the ATM login form")
    public void theCustomerSubmitsTheATMLoginForm() {
        // ATM login is already submitted in @Given step
    }
}
