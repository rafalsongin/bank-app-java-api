# Rafal
Feature: Withdraw from checking account
  As a user
  I want to withdraw money from my checking account
  So that I can access my funds

  Scenario: User withdraws money from checking account
    Given The user is authenticated with email "igmas@gmail.com"
    When The user withdraws 200.0 from the checking account
    Then The response should be "Withdraw was successful"
    And The response status should be 200
