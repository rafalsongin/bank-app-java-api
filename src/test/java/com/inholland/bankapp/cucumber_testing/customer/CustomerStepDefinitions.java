package com.inholland.bankapp.cucumber_testing.customer;

import com.inholland.bankapp.cucumber_testing.BaseStepDefinitions;
import com.inholland.bankapp.dto.LoginDto;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
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
import java.util.Map;
import java.util.Objects;

public class CustomerStepDefinitions extends BaseStepDefinitions {

    // <editor-fold desc="Variables">
    @Autowired
    private TestRestTemplate restTemplate;
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;

    // </editor-fold>

    // <editor-fold desc="Common methods.">
    @Given("The endpoint for {string} is available for method {string} and the customer is logged in")
    public void theEndpointForIsAvailableForMethodAndCustomerIsLoggedIn(String endpoint, String method) {
        // Get a real JWT token for an employee
        String token = getCustomerBearerToken();
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
    public void theCustomerSendsARequestToTheEndpointWithTheFollowingParameters(DataTable dataTable) {
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
            System.out.println("resp: " + response);
            // Assert the IBAN in the response
            Assertions.assertEquals(expectedIban, actualIban);
        } catch (Exception e) {
            Assertions.fail("Failed to parse response body");
        }
    }

    // </editor-fold>

    // <editor-fold desc="Token Retrieval">
    private String getCustomerBearerToken() {
        // Logic to get a Bearer token for an employee
        String loginUrl = "/auth/login"; // The authentication endpoint
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("igmas@gmail.com");
        loginDto.setPassword("igmas12345");

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
    // </editor-fold>

    // <editor-fold desc="Customer gets their transactions.">
    @When("Customer retrieves filtered transactions for their account with endpoint {string}")
    public void customerRetrievesFilteredTransactionsForTheirAccount(String endpoint) {
        response = restTemplate.exchange(
                "/api/" + endpoint,
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                String.class);
    }

    @Then("Customer gets a list of transactions")
    public void customerGetsAListOfTransactions() {
        String body = response.getBody();
        System.out.println(body);
        int actual = JsonPath.read(body, "$.content.length()");
        Assertions.assertTrue(actual >= 1, "Expected at least 1 transaction, but got " + actual);
    }
    // </editor-fold>

    // <editor-fold desc="When - Customer gets their customer details by email">
    @When("The customer sends a GET request to the endpoint with email {string}")
    public void theCustomerSendsAGetRequestToTheEndpointWithEmail(String email) {
        String endpoint = "/api/customers/email/" + email;
        response = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);
    }
    // </editor-fold>

    // <editor-fold desc="When - Customer gets their customer details by id">
    @When("The customer sends a GET request to the endpoint with id {int}")
    public void theCustomerSendsAGETRequestToTheEndpointWithID(int id) {
        String endpoint = "/api/customers/id/" + id;
        System.out.println(endpoint);
        response = restTemplate.exchange(endpoint, HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class);
    }
    // </editor-fold>

    // <editor-fold desc="Then - response: customer details">
    @Then("The response body contains the customer details:")
    public void theResponseBodyContainsTheCustomerDetails(DataTable expectedDataTable) {
        Map<String, String> expectedData = expectedDataTable.asMap(String.class, String.class);
        try {
            String responseBody = response.getBody();

            // Extract values from JSON response and verify each field
            for (Map.Entry<String, String> entry : expectedData.entrySet()) {
                String actualValue = JsonPath.read(responseBody, "$." + entry.getKey()).toString();
                Assertions.assertEquals(entry.getValue(), actualValue, "Mismatch for field: " + entry.getKey());
            }
        } catch (Exception e) {
            Assertions.fail("Failed test: " + e);
        }
    }
    // </editor-fold>
}