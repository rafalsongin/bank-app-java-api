package com.inholland.bankapp.cucumber_testing;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseStepDefinitions {

    @LocalServerPort
    protected int port;
    
    protected String baseUrl;

    @Getter
    @Setter
    protected ResponseEntity<String> response;

    @PostConstruct
    public void init() {
        baseUrl = "http://localhost:" + port;
        System.out.println("Running tests on port: " + port);
    }

    public void theResponseStatusShouldBe(int statusCode) {
        int actualStatusCode = response.getStatusCode().value();
        Assertions.assertEquals(statusCode, actualStatusCode, "Expected status code: " + statusCode + ", but got: " + actualStatusCode);
    }
}
