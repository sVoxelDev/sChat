# sChat

[![Build Status](https://github.com/sVoxelDev/sChat/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/sVoxelDev/sChat?include_prereleases&label=release)](../../releases)
[![codecov](https://codecov.io/gh/sVoxelDev/sChat/branch/master/graph/badge.svg)](https://codecov.io/gh/sVoxelDev/sChat)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

Supercharge the Minecraft Chat Experience of your Players!

* [Installation](#installation)
* [Configuration](#configuration)
* [Commands](#commands)
  * [Player Commands](#player-commands)
  * [Admin Commands](#admin-commands)
* [Permissions](#permissions)
  * [Channel Permissions](#channel-permissions)
* [Plugin Integrations](#plugin-integrations)
  * [Dynamic WorldGuard Region Chats](#dynamic-worldguard-region-chats)
  * [PlaceholderAPI Support](#placeholderapi-support)
* [Frequently Asked Questions](#frequently-asked-questions)
  * [Are HEX colors supported?](#are-hex-colors-supported)

## Installation

**sChat** requires at least **Java 16** and [ProtocolLib][4] must be installed. After these prerequisites are met you
can [download sChat][5] and drop it into the `plugins/` folder. Restart your server and you are done.

## Configuration

All configuration is done inside the `config.yml`. There will always be an
up-to-date [`config.default.yml`](bukkit/src/main/resources/config.default.yml) which contains the latest config values and
default settings. Copy and adjust it to your needs.

You can run `/schat reload` to reload your configuration without restarting the server.

## Commands

### Player Commands

Your players won't need to remember any commands. All they need to do is click on the various UI elements in the chat, *
e.g. clicking on a channel will set the channel as active*. However, here are the commands if you like typing:

| Commands | Alias | Permission | Description |
| -------- | ----- | ---------- | ----------- |
| `/schat channel join <channel>` | `/ch <channel>` | `schat.player.channel.join` | Joins the given channel or sets it as active channel. |
| `/schat channel leave <channel>` | `/leave <channel>` | `schat.player.channel.leave` | Leaves the given channel. |
| `/schat channel message <channel> <message>` | `/ch <channel> <message>` | `schat.player.channel.quickmessage` | Sends a message to the given channel without switching to it. |
| | | | |
| `/schat message send <player> [message]` | `/tell`, `/m`, `/w`, `/msg`, `/pm`, `/qm`, `/dm` | `schat.player.directmessage` | Sends a message to the given player or opens the conversation. |
| | | | |
| `/nickname set <name>` | `/nick <name>` | `schat.nickname.set` | Sets the nickname to the given name. |
| `/nickname set <player> <name>` | `/nick <player> <name>` | `schat.nickname.set.others` | Sets the nickname of another player to the given name. |
| `/nickname reset` | `/nick reset` | `schat.nickname.set` | Resets the nickname to the real player name. |
| `/nickname reset <player>` | `/nick reset <player>` | `schat.nickname.set.others` | Resets the nickname of another player to the real player name. |

### Admin Commands

| Commands | Alias | Permission | Description |
| -------- | ----- | ---------- | ----------- |
| `/schat reload` | | `schat.admin.reload` | Reloads the sChat config and all channels that have changes. This is non disruptive and will not touch unchanged channels. |
| `/schat broadcast <message>` | `/broadcast <message>` | `schat.admin.broadcast` | Sends a message to all channels and private conversations. Every player will get this message. |

## Permissions

| Permission | Description |
| ---------- | ----------- |
| `schat.player` | This permission grouping contains all of the `schat.player.*` permissions. Normally this is the only one you need. |
| `schat.player.channel` | Allows the player to use the `/channel *` commands. |
| `schat.player.channel.join` | Allows the player to join channels. This can be further restricted with individual channel permissions. |
| `schat.player.channel.leave` | Allows the player to leave channels. |
| `schat.player.channel.quickmessage` | Enables the player to send quick messages (`/ch <channel> <message>`) to channels he is allowed to write in. |
| `schat.player.directmessage` | Allows the player to send direct messages (`/dm <player> <message>`) to other players. |
| | |
| `schat.admin` | This permission groups all admin permissions nested under the `schat.admin.*` permissions. OPs get this by default. |
| `schat.admin.reload` | Allows performing the `/schat reload` command to reload the plugin. |
| `schat.admin.broadcast` | Allows performing the `/broadcast` command. |
| | |
| `schat.message.select` | Allows selecting chat messages for moderation or editing. |
| `schat.message.delete` | Allows deleting chat messages. |
| | |
| `schat.nickname.set` | Allows setting a nickname with `/nick`. |
| `schat.nickname.set.others` | Allows to set the nickname of other players with `/nick <player> <name>`. |
| `schat.nickname.set.blocked` | Allows to bypass the nickname pattern and to use blocked nicknames. |

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

## Plugin Integrations

- **Dynamic WorldGuard Region Chats**: Use custom WorldGuard sChat flags on your regions to create a chat channel for
  that region.
- **Vault Prefix & Suffix**: Supports using [Vault][2] prefixes and suffixes. The use of Vault is completely optional.
- **PlaceholderAPI Placeholders**: Channel formats support the use of [PlaceholderAPI][3] placeholders. The use of the
  PlaceholderAPI plugin is completely optional.

### Dynamic WorldGuard Region Chats

Set the WorldGuard region flag `schat:region-chat` to `ALLOW` to create a special chat channel for that region.
Optionally set a name for the chat channel with the `schat:region-chat-name` flag.

1. Create a new region (if you don't have one yet): `/region create my_schat_region`
2. Enable the region chat: `/region flag my_schat_region schat:region-chat allow`
3. Set a custom channel name for the region: `/region flag my_schat_region schat:region-chat-name My cool Region`

Players inside the `my_schat_region` will now automatically get a channel where they can chat with other players inside
that region. If they leave the region they will also automatically leave the channel. You can even further customize
each region channel in the `config.yml` -> `worldguard` section.

### PlaceholderAPI Support

The use of [PlaceholderAPI][2] placeholders in the channel format is possible. For
example `[<channel_name>]%vault_rank% %player_name%: <message>` will work and replace the placeholders.

## Frequently Asked Questions

### Are HEX colors supported?

Yes they are. Use them just like the other colors, e.g. `<#ffffff><message>`. See this [formatting documentation][6] for
more details.

[1]: https://papermc.io/

[2]: https://www.spigotmc.org/resources/vault.34315/

[3]: https://www.spigotmc.org/resources/placeholderapi.6245/

[4]: https://www.spigotmc.org/resources/protocollib.1997/

[5]: https://github.com/Silthus/sChat/releases

[6]: https://docs.adventure.kyori.net/minimessage#format
