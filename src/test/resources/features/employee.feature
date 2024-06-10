Feature: Everything related to employee actions and their management

  #Cezar
  Scenario: View all customer accounts.
    Given The endpoint for "customers" is available for method "GET" and the employee is logged in
    When I retrieve all customers
    Then I get a list of customers
    And I get http status 200

  #Cezar
  Scenario: Approve unverified customer signup and create accounts for the customer.
    Given The endpoint for "customers/approve/27" is available for method "POST" and the employee is logged in
    When I approve the unverified customer signup
    Then Checking and Savings accounts are created for the customer
    And The customer account state is updated to verified
    And I get http status 200


    #Cezar
  Scenario: Decline unverified customer signup and create accounts for the customer.
    Given The endpoint for "customers/decline/27" is available for method "POST" and the employee is logged in
    When I decline the unverified customer signup
    Then The customer account state is updated to declined
    And I get http status 200

  #Cezar
  Scenario: Transfer funds between customers checking accounts.
  Given The endpoint for "transactions" is available for method "POST" and the employee is logged in
  When I transfer funds between customers accounts with the following details:
    | fromAccount         | toAccount            | amount |
    | NL00INHO0342486737  | NL00INHO0392819329   | 100    |
  Then A new transaction is created
    And I get http status 201

  #Cezar
  Scenario: Set daily and absolute transfer limit for customer account
    Given The endpoint for "accounts/NL00INHO0342486737" is available for method "PUT" and the employee is logged in
    When I set the daily and absolute transfer limit for the customer with the following details:
    | accountNumber       | dailyTransferLimit | absoluteTransferLimit |
    | NL00INHO0342486737  | 1000               | 0                     |
    Then The daily and absolute transfer limit for the customer account is updated
    And I get http status 200

  #Mariia
  Scenario: Close a customer account.
    Given The endpoint for "customers/close/27" is available for method "PUT" and the employee is logged in
    When I close the customer account
    Then The customer account is closed
    And I get http status 200

  #Mariia
  Scenario: View a list of all transactions.
    Given The endpoint for "transactions?page=1&size=10&amountCondition=equal&username=rafal.songin%40gmail.com&role=EMPLOYEE" is available for method "GET" and the employee is logged in
    When I retrieve all transactions
    Then I get a list of transactions
    And I get http status 200

  #Mariia
  Scenario: View a list of individual transactions for a customer.
    Given The endpoint for "transactions/account/NL00INHO0854894591?username=rafal.songin%40gmail.com&role=EMPLOYEE" is available for method "GET" and the employee is logged in
    When I retrieve all transactions for a customer
    Then I get a list of customer's transactions
    And I get http status 200