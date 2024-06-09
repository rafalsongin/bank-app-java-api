# Rafal
Feature: Deposit to checking account
  As a user
  I want to deposit money into my checking account
  So that I can increase my balance

  Scenario: User deposits money to checking account
    Given The user is authenticated with email "igmas@gmail.com"
    When The user deposits 500.0 into the checking account
    Then The response should be "Deposit was successful"
    And The response status should be 200
