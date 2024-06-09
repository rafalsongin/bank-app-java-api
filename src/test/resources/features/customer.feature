Feature: All that is related to customer service.

# Cezar's scenario
  Scenario: Find user's IBAN by their first and last name.
    Given The endpoint for "customers/getIbanByCustomerName/Petrica/JeiJei" is available for method "GET" and the customer is logged in
    When The customer sends a request to the endpoint with the following parameters:
      | firstName | lastName |
      | Petrica      | JeiJei      |
    Then The response body contains the following data:
      | iban |
      | NL00INHO0342486737 |
    And Customer gets http status 200