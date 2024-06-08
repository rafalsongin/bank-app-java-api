package com.inholland.bankapp.cucumber_testing.employee;

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
import java.util.*;
import com.inholland.bankapp.cucumber_testing.BaseStepDefinitions;

public class EmployeeStepDefinitions extends BaseStepDefinitions{

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

    @And("I get http status {int}")
    public void iGetHttpStatus(int status) {
        int actual = response.getStatusCode().value();
        Assertions.assertEquals(status, actual);
    }
    // </editor-fold>

    // <editor-fold desc="Testing Scenario: View all customer accounts.">

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
    // </editor-fold>

    // <editor-fold desc="Testing Scenario: Transfer funds between customers accounts.">
    @When("I transfer funds between customers accounts with the following details:")
    public void iTransferFundsBetweenCustomersAccounts(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String fromAccount = data.get(0).get("fromAccount");
        String toAccount = data.get(0).get("toAccount");
        float amount = Float.parseFloat(data.get(0).get("amount"));
        int initiatedBy = 12; // Employee ID
        String transactionType = "External Transaction"; // Transaction type

        // Create transaction DTO
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setFromAccount(fromAccount);
        transactionDto.setToAccount(toAccount);
        transactionDto.setAmount(amount);
        transactionDto.setInitiatedByUser(initiatedBy);
        transactionDto.setTransactionType(transactionType);

        try {
            String requestBody = mapper.writeValueAsString(transactionDto);
            httpHeaders.add("Content-Type", "application/json");
            HttpEntity<String> request = new HttpEntity<>(requestBody, httpHeaders);
            System.out.println("Request body: " + requestBody);
            System.out.println("Request headers: " + request.getHeaders());
            System.out.println("Request: " + request);

            response = restTemplate.exchange(
                    "/api/transactions",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            System.out.println("Response : " + response);
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Failed to create transaction", e);
        }
    }

    @Then("A new transaction is created")
    public void aNewTransactionIsCreated() {
        Assertions.assertNotNull(response, "Response is null");
        String body = response.getBody();
        System.out.println("Response body 1: " + body); // Log the response body for debugging
        Assertions.assertNotNull(response.getBody(), "Response body is null");

        Map<String, Object> transaction = JsonPath.parse(body).read("$");
        // Assert that key fields are present in the response
        Assertions.assertNotNull(transaction.get("transactionType"), "Expected a transaction to be created, but no transaction type found.");
        Assertions.assertEquals("External Transaction", transaction.get("transactionType"), "Transaction type does not match.");
        Assertions.assertEquals(100.0, transaction.get("amount"), "Transaction amount does not match.");
        Assertions.assertEquals("NL00INHO0342486737", transaction.get("fromAccount"), "From account does not match.");
        Assertions.assertEquals("NL00INHO0392819329", transaction.get("toAccount"), "To account does not match.");
    }
    // </editor-fold>

    // <editor-fold desc="Testing Scenario: Set daily and absolute transfer limit for customer account.">
    @When("I set the daily and absolute transfer limit for the customer with the following details:")
    public void iUpdateTheDailyTransferLimitForTheAccount(io.cucumber.datatable.DataTable dataTable) {

        Account account ;
        AccountDto accountDto = new AccountDto();

        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String accountIBAN = data.get(0).get("accountNumber");
        float dailyTransferLimit = Float.parseFloat(data.get(0).get("dailyTransferLimit"));
        float absoluteTransferLimit = Float.parseFloat(data.get(0).get("absoluteTransferLimit"));

        Optional <Account> optionalAccount = accountService.getAccountByIBAN(accountIBAN);
        if (optionalAccount.isEmpty()) {
            throw new RuntimeException("Account not found");
        }
        account = optionalAccount.get();

        accountDto.setDailyTransferLimit(dailyTransferLimit);
        accountDto.setAbsoluteTransferLimit(absoluteTransferLimit);
        accountDto.setIBAN(account.getIBAN());
        accountDto.setAccountId(account.getAccountId());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setBalance(account.getBalance());

        try {
            String requestBody = mapper.writeValueAsString(accountDto);
            httpHeaders.add("Content-Type", "application/json");
            HttpEntity<String> request = new HttpEntity<>(requestBody, httpHeaders);
            System.out.println("Request body: " + requestBody);
            System.out.println("Request headers: " + request.getHeaders());
            System.out.println("Request: " + request);

            response = restTemplate.exchange(
                    "/api/accounts/changeAccount/" + accountIBAN,
                    HttpMethod.PUT,
                    request,
                    String.class
            );
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to update account", e);
        }
    }

    @Then("The daily and absolute transfer limit for the customer account is updated")
    public void theDailyAndAbsoluteTransferLimitForTheCustomerAccountIsUpdated() {
        Assertions.assertNotNull(response, "Response is null");
        String body = response.getBody();
        System.out.println("Response body 1: " + body); // Log the response body for debugging
        Assertions.assertNotNull(response.getBody(), "Response body is null");

        Map<String, Object> account = JsonPath.parse(body).read("$");
        // Assert that key fields are present in the response
        Assertions.assertNotNull(account.get("dailyTransferLimit"), "Expected a daily transfer limit to be updated, but no daily transfer limit found.");
        Assertions.assertEquals(1000.0, account.get("dailyTransferLimit"), "Daily transfer limit does not match.");
        Assertions.assertNotNull(account.get("absoluteTransferLimit"), "Expected an absolute transfer limit to be updated, but no absolute transfer limit found.");
        Assertions.assertEquals(0.0, account.get("absoluteTransferLimit"), "Absolute transfer limit does not match.");
    }
    // </editor-fold>

    // <editor-fold desc="Testing Scenario: Approve unverified customer signup and create accounts for the customer.">

    @When("I approve the unverified customer signup")
    public void iApproveTheUnverifiedCustomerSignup() {
        if (!prepareTheTestDummyCustomer()) {
            throw new RuntimeException("Failed to prepare the test dummy customer");
        }
        response = restTemplate.exchange(
                "/api/customers/approve/" + TEST_USER_ID,
                HttpMethod.POST,
                new HttpEntity<>(null, httpHeaders),
                String.class
        );
    }

    @Then("Checking and Savings accounts are created for the customer")
    public void checkingAndSavingsAccountsAreCreatedForTheCustomer() {
        Assertions.assertNotNull(response, "Response is null");
        String body = response.getBody();
        Assertions.assertNotNull(response.getBody(), "Response body is null");

        List<Account> accounts = accountRepository.getAccountsByCustomerId(TEST_USER_ID);
        Assertions.assertTrue(accounts.size() == 2, "Expected 2 accounts to be created, but got " + accounts.size());

    }

    @And("The customer account state is updated to verified")
    public void theCustomerAccountStateIsUpdatedToVerified() {
        Customer customer = customerService.getCustomerById(TEST_USER_ID).orElse(null);
        Assertions.assertNotNull(customer, "Customer not found");
        Assertions.assertEquals(AccountApprovalStatus.VERIFIED, customer.getAccountApprovalStatus(), "Customer account state is not verified");
    }

    // </editor-fold>

    // <editor-fold desc="Testing Scenario: Decline unverified customer signup and create accounts for the customer.">

    @When("I decline the unverified customer signup")
    public void iDeclineTheUnverifiedCustomerSignup() {
        if (!prepareTheTestDummyCustomer()) {
            throw new RuntimeException("Failed to prepare the test dummy customer");
        }
        response = restTemplate.exchange(
                "/api/customers/decline/" + TEST_USER_ID,
                HttpMethod.POST,
                new HttpEntity<>(null, httpHeaders),
                String.class
        );
    }

    @Then("The customer account state is updated to declined")
    public void theCustomerAccountStateIsUpdatedToDeclined() {
        Customer customer = customerService.getCustomerById(TEST_USER_ID).orElse(null);
        Assertions.assertNotNull(customer, "Customer not found");
        Assertions.assertEquals(AccountApprovalStatus.DECLINED, customer.getAccountApprovalStatus(), "Customer account state is not declined");
    }

    // </editor-fold>

    private boolean prepareTheTestDummyCustomer()
    {
        try {
            // set the cutomer account state to unverified
            // delete all the accounts related to him

            Customer customer = customerService.getCustomerById(TEST_USER_ID).orElse(null);
            if (customer == null) {
                return false;
            }

            customer.setAccountApprovalStatus(AccountApprovalStatus.UNVERIFIED);
            customerRepository.save(customer);

            List<Account> accounts = accountRepository.getAccountsByCustomerId(TEST_USER_ID);

            if (accounts.isEmpty()) {
                return true;
            }

            for (Account account : accounts) {
                accountRepository.deleteById(account.getAccountId());
            }
        }
        catch (Exception e) {
            return false;
        }

        return true;
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


