Feature: Everything related to employee actions and their management

  #Cezar
  Scenario: View all customer accounts.
    Given The endpoint for "customers" is available for method "GET" and the employee is logged in
    When I retrieve all customers
    Then I get a list of customers
    And I get http status 200

  #Cezar
  Scenario: Approve unverified customer signup and create accounts for the customer.


  #Cezar
  Scenario: Transfer funds between customers checking accounts.
  Given The endpoint for "transactions" is available for method "POST" and the employee is logged in
  When I transfer funds between customers accounts with the following details:
    | fromAccount         | toAccount            | amount |
    | NL00INHO0342486737  | NL00INHO0392819329   | 100    |
  Then A new transaction is created
    And I get http status 201

  #Cezar
  Scenario: Set daily transfer limit for customer


  #Cezar
  Scenario: Set absolute transfer limit for customer.




  Scenario: View a list of unverified customers.

  Scenario: Close a customer account.

  Scenario: View a list of all transactions.

  Scenario: View a list of individual transactions for a customer.


