Feature: Cross Server Global Chatting
  Users can chat across server using global channels.

  Background: Multiple Servers
    Given a second server 'server2'
    And a global channel 'global'
    And the following users
      | name    | server  | channel |
      | player1 | server1 | global  |
      | player2 | server2 | global  |

  Scenario: Users can chat across two servers
    When user 'player1' sends a message
    Then user 'player2' receives the message