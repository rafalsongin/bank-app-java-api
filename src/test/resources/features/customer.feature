Feature: All that is related to customer service.

# Cezar's scenario
  Scenario: Find user's IBAN by their first and last name.
    Given The endpoint for "customers/iban/Petrica/JeiJei" is available for method "GET" and the customer is logged in
    When The customer sends a request to the endpoint with the following parameters:
      | firstName | lastName |
      | Petrica      | JeiJei      |
    Then The response body contains the following data:
      | iban |
      | NL00INHO0342486737 |
    And Customer gets http status 200



  #Mariia
  Scenario: Customer view their transactions for a specific account
    Given The endpoint for "transactions/account/NL00INHO0854894591?username=igmas%40gmail.com&role=CUSTOMER" is available for method "GET" and the customer is logged in
    When Customer retrieves all transactions for their account
    Then Customer gets a list of transactions
    And Customer gets http status 200


    #Mariia
  Scenario: Customer view their transactions greater than a specific amount
    Given The endpoint for "transactions/account/NL00INHO0854894591?username=igmas%40gmail.com&role=CUSTOMER&amountCondition=greaterThan&amount=200" is available for method "GET" and the customer is logged in
    When Customer retrieves all transactions greater than 200 for their account
    Then Customer gets a list of transactions greater than 200
    And Customer gets http status 200