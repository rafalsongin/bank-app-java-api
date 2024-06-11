package com.inholland.bankapp.cucumber_testing.atm;

import com.inholland.bankapp.cucumber_testing.BaseStepDefinitions;
import com.inholland.bankapp.dto.AtmTransactionDto;
import com.inholland.bankapp.dto.LoginDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AtmStepDefinitions extends BaseStepDefinitions {

    @Autowired
    private RestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();
    private static final Logger logger = Logger.getLogger(AtmStepDefinitions.class.getName());
    private Exception exception;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Given("The user is authenticated with email {string}")
    public void theUserIsAuthenticated(String email) {
        // Simulate authentication by obtaining a JWT token
        String token = authenticateCustomer(email, "igmas12345"); // Replace "password" with the actual password or parameterize it
        headers.set("Authorization", "Bearer " + token);
        logger.info("Authenticated with token: " + token);
        logger.info("Headers after authentication: " + headers);
    }

    private String authenticateCustomer(String email, String password) {
        String loginUrl = baseUrl + "/auth/login-atm"; // The authentication endpoint
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
            response = restTemplate.exchange(baseUrl + "/atm/balance", HttpMethod.GET, entity, String.class);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            exception = e;
            logger.severe("Error during balance retrieval: " + e.getResponseBodyAsString());
        }
    }

    @Then("The balance should be {double}")
    public void theBalanceShouldBe(double expectedBalance) {
        if (exception != null) {
            Assertions.fail("Exception occurred: " + exception.getMessage());
        }
        Assertions.assertNotNull(response.getBody(), "Response body is null");
        double balance = Double.parseDouble(response.getBody());
        assertEquals(expectedBalance, balance);
    }

    @When("The user deposits {double} into the checking account")
    public void theUserDepositsIntoTheCheckingAccount(double amount) {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(amount);
        HttpEntity<AtmTransactionDto> entity = new HttpEntity<>(atmTransactionDto, headers);
        logger.info("Request headers for deposit: " + entity.getHeaders());
        try {
            response = restTemplate.postForEntity(baseUrl + "/atm/deposit", entity, String.class);
        } catch (HttpClientErrorException e) {
            exception = e;
            logger.severe("Error during deposit: " + e.getResponseBodyAsString());
        }
    }

    @And("The response should be {string}")
    public void theResponseShouldBe(String expectedResponse) {
        if (exception != null) {
            Assertions.fail("Exception occurred: " + exception.getMessage());
        }
        assertEquals(expectedResponse, response.getBody());
    }

    @When("The user withdraws {double} from the checking account")
    public void theUserWithdrawsFromTheCheckingAccount(double amount) {
        AtmTransactionDto atmTransactionDto = new AtmTransactionDto();
        atmTransactionDto.setAmount(amount);
        HttpEntity<AtmTransactionDto> entity = new HttpEntity<>(atmTransactionDto, headers);
        try {
            response = restTemplate.postForEntity(baseUrl + "/atm/withdraw", entity, String.class);
        } catch (HttpClientErrorException e) {
            exception = e;
            logger.severe("Error during withdrawal: " + e.getResponseBodyAsString());
        }
    }

    @Then("The response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        if (exception != null) {
            Assertions.assertTrue(exception instanceof HttpClientErrorException, "Expected HttpClientErrorException but got: " + exception.getClass().getName());
            HttpClientErrorException clientErrorException = (HttpClientErrorException) exception;
            Assertions.assertEquals(status, clientErrorException.getStatusCode().value(), "Status code does not match");
        } else {
            Assertions.assertNotNull(response, "Response is null");
            int actual = response.getStatusCode().value();
            Assertions.assertEquals(status, actual);
        }
    }
}
