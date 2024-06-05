package com.inholland.bankapp.cucumber_testing.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inholland.bankapp.dto.LoginDto;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EmployeeStepDefinitions extends BaseStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    private HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;

    @Given("The endpoint for {string} is available for method {string} and the employee is logged in")
    public void theEndpointForIsAvailableForMethodAndEmployeeIsLoggedIn(String endpoint, String method) {
        // Get a real JWT token for an employee
        String token = getEmployeeBearerToken();
        httpHeaders.set("Authorization", "Bearer " + token);

        response = restTemplate.exchange(
                "/api/" + endpoint,
                HttpMethod.OPTIONS,
                new HttpEntity<>(null, httpHeaders),
                String.class);

        List<String> options = Arrays.stream(Objects.requireNonNull(response.getHeaders()
                        .get("Allow"))
                .get(0)
                .split(",")).toList();

        Assertions.assertTrue(options.contains(method.toUpperCase()));
    }

    @When("I retrieve all customers")
    public void iRetrieveAllCustomers() {
        response = restTemplate.exchange(
                "/api/customers",
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                String.class
        );
    }

    @Then("I get a list of customers")
    public void iGetAListOfCustomers() {
        String body = response.getBody();
        int actual = JsonPath.read(body, "$.length()");
        Assertions.assertTrue(actual >= 1, "Expected at least 1 customer, but got " + actual);
    }

    @And("I get http status {int}")
    public void iGetHttpStatus(int status) {
        int actual = response.getStatusCode().value();
        Assertions.assertEquals(status, actual);
    }

    private String getEmployeeBearerToken() {
        // Logic to get a Bearer token for an employee
        String loginUrl = "/auth/login"; // The authentication endpoint
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("rafal.songin@gmail.com");
        loginDto.setPassword("rafalsongin");

        HttpEntity<LoginDto> loginRequest = new HttpEntity<>(loginDto, loginHeaders);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, String.class);
        if (loginResponse.getStatusCode() == HttpStatus.OK) {
            // Extract token from response body
            String token = loginResponse.getBody();
            return token;
        } else {
            throw new RuntimeException("Failed to login and get token");
        }
    }
}


