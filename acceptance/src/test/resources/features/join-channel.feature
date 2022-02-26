Feature: Join Channel
  Users can join channels using the /ch <channel> command.

  Scenario: User can join public channel
    Given a public channel global
    When I execute '/ch global'
    Then I am a member of the global channel
    And the global channel is active

  Scenario: User cannot join protected channel
    Given a protected channel protected
    When I execute '/ch protected'
    Then I am not a member of the protected channel
    And I received the cannot join protected channel message

  Scenario: User can join protected channel
    Given a protected channel protected
    And I have the 'schat.channel.protected.join' permission
    When I execute '/ch protected'
    Then I am a member of the protected channel