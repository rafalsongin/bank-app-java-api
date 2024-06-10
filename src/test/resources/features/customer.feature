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


  #Mariia - get filtered transactions for a specific account for a customer
  Scenario: Customer views all transactions for a specific account
    Given The endpoint for "transactions/account/NL00INHO0854894591?username=igmas%40gmail.com&role=CUSTOMER" is available for method "GET" and the customer is logged in
    When Customer retrieves filtered transactions for their account with endpoint "transactions/account/NL00INHO0854894591?username=igmas@gmail.com&role=CUSTOMER"
    Then Customer gets a list of transactions
    And Customer gets http status 200

  Scenario: Customer views transactions greater than a specific amount
    Given The endpoint for "transactions/account/NL00INHO0127227054?username=igmas%40gmail.com&role=CUSTOMER&amountCondition=greaterThan&amountValue=200" is available for method "GET" and the customer is logged in
    When Customer retrieves filtered transactions for their account with endpoint "transactions/account/NL00INHO0127227054?username=igmas@gmail.com&role=CUSTOMER&amountCondition=greaterThan&amountValue=200"
    Then Customer gets a list of transactions
    And Customer gets http status 200

  Scenario: Customer views transactions within a date range
    Given The endpoint for "transactions/account/NL00INHO0127227054?username=igmas%40gmail.com&role=CUSTOMER&startDate=2024-05-01&endDate=2024-07-31" is available for method "GET" and the customer is logged in
    When Customer retrieves filtered transactions for their account with endpoint "transactions/account/NL00INHO0127227054?username=igmas@gmail.com&role=CUSTOMER&startDate=2024-05-01&endDate=2024-07-31"
    Then Customer gets a list of transactions
    And Customer gets http status 200

  Scenario: Customer views transactions from a specific sender account
    Given The endpoint for "transactions/account/NL00INHO0127227054?username=igmas%40gmail.com&role=CUSTOMER&fromIban=NL00INHO0854894591" is available for method "GET" and the customer is logged in
    When Customer retrieves filtered transactions for their account with endpoint "transactions/account/NL00INHO0127227054?username=igmas@gmail.com&role=CUSTOMER&fromIban=NL00INHO0854894591"
    Then Customer gets a list of transactions
    And Customer gets http status 200

  Scenario: Customer views transactions to a specific receiver account
    Given The endpoint for "transactions/account/NL00INHO0127227054?username=igmas%40gmail.com&role=CUSTOMER&toIban=NL00INHO0854894591" is available for method "GET" and the customer is logged in
    When Customer retrieves filtered transactions for their account with endpoint "transactions/account/NL00INHO0127227054?username=igmas@gmail.com&role=CUSTOMER&toIban=NL00INHO0854894591"
    Then Customer gets a list of transactions
    And Customer gets http status 200