Feature: Cross Server Global Chatting
  Users can chat across server using global channels.

  Scenario: User can chat across two servers
    Given a global channel named 'global'
    Given a user 'A'
    And user 'A' is on 'Server 1'
    And user 'A' is member of channel 'global'
    Given a user 'B'
    And user 'B' is in 'Server 2'
    And user 'B' is member of channel 'global'
    When user 'A' sends the message 'Hello'
    Then user 'B' receives the message 'Hello'