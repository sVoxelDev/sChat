Feature: Join Channel
  Users can join channels using the /ch <channel> command.

  Scenario: User can join public channel
    Given a public channel 'global'
    Given a user
    And user has no permissions
    When user runs '/ch global'
    Then user is member of channel 'global'
    Then user received joined channel 'global' message

  Scenario: User cannot join protected channel
    Given a protected channel 'protected'
    Given a user
    And user has no permissions
    When user runs '/ch protected'
    Then user is not a member of channel 'protected'
    Then user receives join channel error message for channel 'protected'

  Scenario: User can join protected channel
    Given a protected channel 'protected'
    Given a user
    And user has permission 'schat.channel.protected.join'
    When user runs '/ch protected'
    Then user is member of channel 'protected'
    Then user received joined channel 'protected' message