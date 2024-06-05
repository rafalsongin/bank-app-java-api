Feature: Everything related to employee actions and their management

  Scenario: View all customer accounts.


  Scenario: Approve unverified customer signup and create accounts for the customer.

  Scenario: View a list of unverified customers.

  Scenario: Transfer funds between customers accounts.

  Scenario: Set absolute transfer limit for customer.

  Scenario: Set daily transfer limit for customer
    Given the customer accounts are available
    When I set the daily transfer limit for a customer
    Then the daily transfer limit is set for the customer


  Scenario: Close a customer account.

  Scenario: View a list of all transactions.

    #example     Given The endpoint for "guitars" is available for method "GET"
    #    When I retrieve all guitars
    #    Then I get a list of 3 guitars
    #    And I get http status 200
