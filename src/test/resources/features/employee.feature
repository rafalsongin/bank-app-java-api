Feature: Everything related to employee actions and their management

  Scenario: View all customer accounts.
    Given The endpoint for "customers" is available for method "GET" and the employee is logged in
    When I retrieve all customers
    Then I get a list of customers
    And I get http status 200


  Scenario: Approve unverified customer signup and create accounts for the customer.

  Scenario: View a list of unverified customers.

  Scenario: Transfer funds between customers accounts.

  Scenario: Set daily transfer limit for customer
#    Given The endpoint for updating customer "40" is available for method "PUT" and the employee is logged in
#    When I update the daily transfer limit for customer from "1000" to "2000"
#    Then I get http status 200
#    And The account is updated

  Scenario: Set absolute transfer limit for customer.
#    Given The endpoint for updating customer "2" is available for method "PUT"
#    When I update the absolue transfer limit for customer from "0" to "-100"
#    Then I get http status 200
#    And The account is updated


  Scenario: Close a customer account.

  Scenario: View a list of all transactions.

    #example     Given The endpoint for "guitars" is available for method "GET"
    #    When I retrieve all guitars
    #    Then I get a list of 3 guitars
    #    And I get http status 200
