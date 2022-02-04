Feature: Private Chats
  Players can chat privately with each other.

  Background: Two users
    Given the following users
      | name    | server  |
      | player1 | server1 |
      | player2 | server1 |
      | player3 | server1 |

  Scenario: Users can send private messages
    When I send a private message to player2
    Then player2 receives the message
    And the view of player2 shows the message of player1 in a separate tab
    And player3 does not receive a message