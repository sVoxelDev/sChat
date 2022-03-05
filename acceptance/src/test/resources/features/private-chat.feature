Feature: Private Chats
  Players can chat privately with each other.

  Background: Two users
    Given the following users
      | name    | server  |
      | player1 | server1 |
      | player2 | server1 |
      | player3 | server1 |
      | player4 | server2 |
    And I am player1

  Scenario: Users can send private messages
    When I send a private message to player2
    Then player2 receives the message
    And my view shows the message
    But player3 does not receive a message

  Scenario: Players can send private messages across servers
    When I send a private message to player4
    Then player4 receives the message