Feature: Hello World

  Scenario: Say Hello World
    Given a user
    When the user calls the service
    Then the response is "Hello World!"

  Scenario Outline: Say Hello Name
    Given a user with name "<name>"
    When the user calls the service with his name
    Then the response is "Hello <name>!"

    Examples:
      | name    |
      | Christian |
      | Max |
      | Stephan |
