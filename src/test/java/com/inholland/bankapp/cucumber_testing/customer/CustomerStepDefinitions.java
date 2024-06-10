package com.inholland.bankapp.cucumber_testing.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inholland.bankapp.dto.AccountDto;
import com.inholland.bankapp.dto.LoginDto;
import com.inholland.bankapp.model.Account;
import com.inholland.bankapp.model.AccountApprovalStatus;
import com.inholland.bankapp.model.Customer;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import com.inholland.bankapp.dto.TransactionDto;
import com.inholland.bankapp.service.AccountService;
import com.inholland.bankapp.service.CustomerService;
import com.inholland.bankapp.repository.CustomerRepository;
import com.inholland.bankapp.repository.AccountRepository;


import java.io.IOException;
import java.util.*;
import com.inholland.bankapp.cucumber_testing.BaseStepDefinitions;

import io.cucumber.java.en.Given;

public class CustomerStepDefinitions extends BaseStepDefinitions {

    // <editor-fold desc="Variables">
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;

    private HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;

    private final static int TEST_USER_ID = 27;

    // </editor-fold>

    // <editor-fold desc="Common methods.">
    @Given("The endpoint for {string} is available for method {string} and the customer is logged in")
    public void theEndpointForIsAvailableForMethodAndCustomerIsLoggedIn(String endpoint, String method) {
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

    @And("Customer gets http status {int}")
    public void customerGetHttpStatus(int status) {
        int actual = response.getStatusCode().value();
        Assertions.assertEquals(status, actual);
    }
    // </editor-fold>

    // <editor-fold desc="Find user's IBAN by their first and last name.">
    @When("The customer sends a request to the endpoint with the following parameters:")
    public void theCustomerSendsARequestToTheEndpointWithTheFollowingParameters(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String firstName = data.get(0).get("firstName");
        String lastName = data.get(0).get("lastName");

        String endpoint = String.format("/api/customers/iban/%s/%s", firstName, lastName);

        // Make the GET request
        response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                String.class);
    }

    @Then("The response body contains the following data:")
    public void theResponseBodyContainsTheFollowingData(io.cucumber.datatable.DataTable expectedDataTable) {
        Map<String, String> expectedData = expectedDataTable.asMaps(String.class, String.class).get(0);
        String expectedIban = expectedData.get("iban");

        try {
            String actualIban = response.getBody();
            // Assert the IBAN in the response
            Assertions.assertEquals(expectedIban, actualIban);
        } catch (Exception e) {
            Assertions.fail("Failed to parse response body");
        }
    }

    // </editor-fold>

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


    // <editor-fold desc="Customer gets their transactions.">
    @When("Customer retrieves all transactions for their account")
    public void customerRetrievesAllTransactionsForTheirAccount() {
        response = restTemplate.exchange(
                "/api/transactions/account/NL00INHO0854894591?username=igmas@gmail.com&role=CUSTOMER",
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                String.class);
    }

    @Then("Customer gets a list of transactions")
    public void customerGetsAListOfTransactions() {
        String body = response.getBody();
        int actual = JsonPath.read(body, "$.content.length()");
        Assertions.assertTrue(actual >= 1, "Expected at least 1 transaction, but got " + actual);
    }

    @When("Customer retrieves all transactions greater than 200 for their account")
    public void customerRetrievesAllTransactionsGreaterThanForTheirAccount() {
        response = restTemplate.exchange(
                "/api/transactions/account/NL00INHO0127227054?username=igmas@gmail.com&role=CUSTOMER&amountCondition=greaterThan&amount=200",
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                String.class);
    }

    @Then("Customer gets a list of transactions greater than 200")
    public void customerGetsAListOfTransactionsGreaterThan() {
        String body = response.getBody();
        System.out.println(body);
        int actual = JsonPath.read(body, "$.content.length()");
        Assertions.assertTrue(actual >= 1, "Expected at least 1 transaction, but got " + actual);
    }
    // </editor-fold>
}