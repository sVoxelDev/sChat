Feature: Cross Server Global Chatting
  Users can chat across server using global channels.

  Background: Multiple Servers
    Given a global channel 'global'
    And the following users
      | name    | server  | channel |
      | player1 | server1 | global  |
      | player2 | server2 | global  |
    And I am player1

  Scenario: Users can chat across two servers
    When I send a message
    Then the view of player2 shows the message

  Scenario: Chatting in global channel with custom format
    Given the channel global has a custom format
    When I send a message
    Then the view of player2 shows the message