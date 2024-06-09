package com.inholland.bankapp.cucumber_testing;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/auth.feature",
        glue = {
                "com.inholland.bankapp.cucumber_testing.auth",
                "com.inholland.bankapp.cucumber_testing"
        }
)
public class AuthFeatureTest {
}
