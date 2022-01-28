Feature: Auto-Join Channels
  Users should auto joint previous and public channels when they join the server.

  Scenario: First-time user auto joins public channels
    Given a user
    And user has not joined the server
    Given a public channel 'global'
    When user joins the game
    Then user is member of channel 'global'
