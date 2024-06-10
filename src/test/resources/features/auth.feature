Feature: Authentication and Registration

  Scenario: Register a new customer
    Given The following customer registration details
      | email           | password  | firstName  | lastName  | bsn        | phoneNumber  |
      | test+x@test.com | password1 | Johna      | Doae      | 112233445  | 0612345678   |
    When The customer submits the registration form
    Then The response status should be 200
    And The response message should be "Customer registration form submitted successfully"

  Scenario: Register an existing customer
    Given The following customer registration details
      | email           | password  | firstName | lastName | bsn        | phoneNumber  |
      | igmas@gmail.com | password1 | John      | Doe      | 123456789  | 0612345678   |
    When The customer submits the registration form
    Then The response status should be 409
    And The response message should be "Customer with igmas@gmail.com email already exists."

  Scenario: Login with valid credentials
    Given The following login details
      | email                   | password  |
      | rafal.songin@gmail.com  | password1 |
    When The user submits the login form
    Then The response status should be 200
    And The response should contain a valid JWT token

  Scenario: Login with invalid credentials
    Given The following login details
      | email          | password  |
      | test@test.com  | wrongpass |
    When The user submits the login form
    Then The response status should be 401
    And The response message should be "Invalid email or password"

  Scenario: Login ATM with valid customer credentials
    Given The following login details
      | email           | password   |
      | igmas@gmail.com | igmas12345 |
    When The customer submits the ATM login form
    Then The response status should be 200
    And The response should contain a valid JWT token

  Scenario: Login ATM with non-customer credentials
    Given The following login details
      | email                  | password  |
      | rafal.songin@gmail.com | rafalsongin |
    When The customer submits the ATM login form
    Then The response status should be 401
    And The response message should be "User is not a customer"
