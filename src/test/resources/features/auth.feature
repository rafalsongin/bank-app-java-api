Feature: Authentication and Registration

  Scenario: Register a new customer
    Given The following customer registration details
      | email            | password  | firstName  | lastName  | bsn        | phoneNumber  |
      | test+xx@test.com | password1 | Johna      | Doae      | 112233445  | 0612345678   |
    When The customer submits the registration form
    Then The response status code should be 200

  Scenario: Register an existing customer
    Given The following customer registration details
      | email           | password  | firstName | lastName | bsn        | phoneNumber  |
      | igmas@gmail.com | password1 | John      | Doe      | 123456789  | 0612345678   |
    When The customer submits the registration form
    Then The response status code should be 409

  Scenario: Login with valid credentials
    Given The following login details
      | email                   | password    |
      | rafal.songin@gmail.com  | rafalsongin |
    When The user submits the login form
    Then The response status code should be 200

  Scenario: Login with invalid credentials
    Given The following login details
      | email                   | password  |
      | rafal.songin@gmail.com  | wrongpass |
    When The user submits the login form
    Then The response status code should be 401

  Scenario: Login ATM with valid customer credentials
    Given The following login details
      | email           | password   |
      | igmas@gmail.com | igmas12345 |
    When The customer submits the ATM login form
    Then The response status code should be 200
