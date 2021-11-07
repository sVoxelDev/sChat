# sChat

[![Build Status](https://github.com/Silthus/sChat/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Silthus/sChat?include_prereleases&label=release)](../../releases)
[![codecov](https://codecov.io/gh/Silthus/sChat/branch/master/graph/badge.svg)](https://codecov.io/gh/Silthus/sChat)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

Chat like never before!

## Configuration

All configuration is done inside the `config.yml`. There will always be an
up-to-date [`config.defaults.yml`](src/main/resources/config.defaults.yml) which contains the latest config values and
default settings. Copy and adjust it to your needs.

## Commands

Your players don't need to remember any commands. All they need to do is click on the various UI elements in the chat.
For example: clicking on a channel will set the channel as active.

## Permissions

| Permission | Description |
| ---------- | ----------- |
| `schat.player` | This permission grouping contains all of the `schat.player.*` permissions. Normally this is the only one you need. |
| `schat.player.channel` | Allows the player to use the `/channel *` commands. |
| `schat.player.channel.join` | Allows the player to join channels. This can be further restricted with individual channel permissions. |
| `schat.player.channel.quickmessage` | Enables the player to send quick messages (`/ch <channel> <message>`) to channels he is allowed to write in. |
| `schat.player.directmessage` | Allows the player to send direct messages (`/dm <player> <message>`) to other players. |

### Channel Permissions

Every channel that has the `protect: true` flag set will be assigned a `schat.channel.<channel_id>` permission. This
permission is then required to join the channel.

For example the `team` channel requires the `schat.channel.team` permission to join it.

```yaml
channels:
  team: # <-- this is the id of the channel 
    name: My cool Team
    protect: true
```