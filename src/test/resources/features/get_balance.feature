# Rafal
Feature: Get checking account balance
  As a user
  I want to get the balance of my checking account
  So that I can see how much money I have

  Scenario: User retrieves checking account balance
    Given The user is authenticated with email "igmas@gmail.com"
    When The user retrieves the checking account balance
#    Then The balance should be 1620.0
    And The response status should be 200
