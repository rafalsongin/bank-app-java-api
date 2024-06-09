package com.inholland.bankapp.cucumber_testing;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CommonStepDefinitions {
    private ResponseEntity<String> response;

    public ResponseEntity<String> getResponse() {
        return response;
    }

    public void setResponse(ResponseEntity<String> response) {
        this.response = response;
    }
}
