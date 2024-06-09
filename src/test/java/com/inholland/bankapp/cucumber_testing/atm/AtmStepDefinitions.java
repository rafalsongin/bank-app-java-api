package com.inholland.bankapp.cucumber_testing.atm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inholland.bankapp.cucumber_testing.CommonStepDefinitions;
import com.inholland.bankapp.dto.AtmTransactionDto;
import com.inholland.bankapp.dto.LoginDto;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

public class AtmStepDefinitions {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommonStepDefinitions commonStepDefinitions;

    private HttpHeaders headers = new HttpHeaders();
    private ResponseEntity<String> response;
    private String baseUrl = "http://localhost:" + 8080 + "/atm";

    private static final Logger logger = Logger.getLogger(AtmStepDefinitions.class.getName());

    @Given("The user is authenticated with email {string}")
    public void theUserIsAuthenticated(String email) {
        // Simulate authentication by obtaining a JWT token
        String token = authenticateCustomer(email, "igmas12345"); // Replace "password" with the actual password or parameterize it
        headers.set("Authorization", "Bearer " + token);
        logger.info("Authenticated with token: " + token);
        logger.info("Headers after authentication: " + headers);
    }

    private String authenticateCustomer(String email, String password) {
        String loginUrl = "http://localhost:8080/auth/login-atm"; // The authentication endpoint
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(email);
        loginDto.setPassword(password);

        HttpEntity<LoginDto> loginRequest = new HttpEntity<>(loginDto, loginHeaders);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, String.class);
        if (loginResponse.getStatusCode() == HttpStatus.OK) {
            // Extract token from response body
            return loginResponse.getBody();
        } else {
            throw new RuntimeException("Failed to login and get token: " + loginResponse.getStatusCode());
        }
    }

    @When("The user retrieves the checking account balance")
    public void theUserRetrievesTheCheckingAccountBalance() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            response = restTemplate.exchange(baseUrl + "/balance", HttpMethod.GET, entity, String.class);
            commonStepDefinitions.setResponse(response);
        } catch (HttpServerErrorException e) {
            logger.severe("Error during balance retrieval: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    @Then("The balance should be {double}")
    public void theBalanceShouldBe(double expectedBalance) {
        response = commonStepDefinitions.getResponse();
        Assertions.assertNotNull(response.getBody(), "Response body is null");
        double balance = Double.parseDouble(response.getBody());
        Assertions.assertEquals(expectedBalance, balance);
    }

    @And("The response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        response = commonStepDefinitions.getResponse();
        Assertions.assertEquals(statusCode, response.getStatusCodeValue());
    }

    @When("The user deposits {double} into the checking account")
    public void theUserDepositsIntoTheCheckingAccount(double amount) {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(amount);
        HttpEntity<AtmTransactionDto> entity = new HttpEntity<>(atmTransactionDto, headers);
        logger.info("Request headers for deposit: " + entity.getHeaders());
        try {
            response = restTemplate.postForEntity(baseUrl + "/deposit", entity, String.class);
            commonStepDefinitions.setResponse(response);
        } catch (HttpClientErrorException e) {
            logger.severe("Error during deposit: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    @Then("The response should be {string}")
    public void theResponseShouldBe(String expectedResponse) {
        response = commonStepDefinitions.getResponse();
        Assertions.assertEquals(expectedResponse, response.getBody());
    }

    @When("The user withdraws {double} from the checking account")
    public void theUserWithdrawsFromTheCheckingAccount(double amount) {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(amount);
        HttpEntity<AtmTransactionDto> entity = new HttpEntity<>(atmTransactionDto, headers);
        try {
            response = restTemplate.postForEntity(baseUrl + "/withdraw", entity, String.class);
            commonStepDefinitions.setResponse(response);
        } catch (HttpClientErrorException e) {
            logger.severe("Error during withdrawal: " + e.getResponseBodyAsString());
            throw e;
        }
    }
}
