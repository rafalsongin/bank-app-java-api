Feature: All that is related to customer service.

# Cezar's scenario
  Scenario: Find user's IBAN by their first and last name.
    Given The endpoint for "customers/getIbanByCustomerName/First/Last" is available for method "GET" and the customer is logged in
    When The employee sends a request to the endpoint with the following parameters:
      | firstName | lastName |
      | First      | Last      |
    Then The response body contains the following data:
      | iban |
      | NL00INHO0681563800 |
    And Customer gets http status 200

# Mariia's scenario
  Scenario: Search and filter transactions.
