# Channels

Channels are the core component of sChat and organize the message flood into tabs. The channels need to be configured in the `config.yml` under the `channels` section.
Every channel can have a range of settings that are explained below. [Other plugins][developer] can extend that functionality and add custom settings.

!!! important
    Global channels must be configured the same on all servers to work properly.

You can use the `/schat reload` [command][commands] to reload your channels on the fly after you have made changes.

```yaml
channels: # all channels must go under the channels section
  my_channel: # every channel needs a unique key that must only contain letters, numbers, underscores or dashes
    name: My Channel # the channel also needs a name that can be of any format and is parsed as a minimessage
    settings:
      ... # all channel specific settings go here
```

## Settings

Every channel can have any or all of the following settings.  

!!! note
    Default values are marked with a :octicons-pin-24:.

### `priority`

[:octicons-milestone-24: next][next] · `int` | :octicons-pin-24: `100`

The priority of the channel controls its position in the tabs and commands.  
The lower the `priority` value the higher up the channel appears.
Channels with the same priority are sorted by their name.

??? example
    ```yaml
    channels:
    aaa:
        settings:
        priority: 20
    bbb:
        settings:
        priority: 10
    cbb:
        name: ccc
    caa:
        name: ccc
    ```

    The channels are sorted in the following way:

    ```
    | bbb | aaa | caa | cbb |
    ```

### `protected`

[:octicons-milestone-24: next][next] · `true` | :octicons-pin-24: `false`

Protected channels can only be joined by players that have the `schat.channel.<key>.join` permission.  

For example the `team` channel requires the `schat.channel.team.join` permission.

### `join_permission`

[:octicons-milestone-24: next][next] · `string` | :octicons-pin-24: `schat.channel.<key>.join`

You can override the required join permission with this setting.

!!! info
    This setting only works if `protected: true` is also set.

### `global`

[:octicons-milestone-24: next][next] · :octicons-pin-24: `true` | `false`

Messages that are sent to a `global` channel are forwarded with the [configured messenger][messenger].  

### `hidden`

[:octicons-milestone-24: next][next] · `true` | :octicons-pin-24: `false`

Hides the channel in commands, auto completion and listings.  
Does not prevent joining or leaving the channel.  
Players that are joined to the channel will see it in the channel tabs like all other channels.

### `auto_join`

[:octicons-milestone-24: next][next] · `true` | :octicons-pin-24: `false`

If enabled players will automatically join the channel when they join the server.  
Only channels the player has access to will be auto joined.

!!! note
    If the players leaves an `auto_join` channel and rejoins the server, the channel is joined again.  
    This option is therefor best combined with the `forced` setting.

### `forced`

[:octicons-milestone-24: next][next] · `true` | :octicons-pin-24: `false`

Forced channels cannot be left by players.

## Format

The following settings are all formatting options and parsed with the [MiniMessage format][minimessage].

They go under the normal `settings` section like all other options.

### `message_format`

[:octicons-milestone-24: next][next] · :octicons-beaker-24: Experimental · `string`

The message format controls how message are displayed for the given channel.  
In addition to the [PlacerholderAPI][placeholderapi], the following placeholders are supported:

!!! tip
    With sChat you have something completly new: **Tabbed Chat Channels**  
    This gives you the power to declutter your chat and remove the now unneeded `[Channel]` prefix from the messages.

| Placeholder | Description | Example | Since |
| ----------- | ------- | -------- | -------: |
| `<id>` | The unique id of the message. | `66ad5e0d-ed28-4601-bcde-617b6729b5b3` | [:octicons-milestone-24: next][next] |
| `<timestamp>` | The timestamp when the message was sent. | `2021-05-01T12:10:55.412386200Z` | [:octicons-milestone-24: next][next] |
| `<type>` | The type of the message. | `SYSTEM` or `CHAT` | [:octicons-milestone-24: next][next] |
| `<text>` | The text of the message. | `Hi there!` | [:octicons-milestone-24: next][next] |
| `<source_uuid>` | The ID of the message sender. | `dd5c4f63-b5d9-43e7-9584-40b25494d7e8` | [:octicons-milestone-24: next][next] |
| `<source_name>` | The name of the message sender. | `Silthus` | [:octicons-milestone-24: next][next] |
| `<source_display_name>` | The formatted display or nickname of the sender. | `&aSilthus` | [:octicons-milestone-24: next][next] |
| `<channel_key>` | The unique key of the channel. | `global` | [:octicons-milestone-24: next][next] |
| `<channel_display_name>` | The formatted name of the channel. | `Global` | [:octicons-milestone-24: next][next] |

??? example "Example with Name Hover and PlaceholderAPI"

```yaml channels:
global:
name: <green>Global settings:
message_format: "<aqua>[<channel_name>]</aqua>%vault_prefix%<hover:show_text:'<source_display_name>\n<gray>Rank: <aqua>%vault_rank%'><source_display_name></hover>%vault_suffix%<gray>: <text>"
```

    ![Channel Message Format](images/channels-message_format.png)
    ![Channel Message Format Hover](images/channels-message_format_hover.png)

[next]: https://github.com/sVoxelDev/sChat/releases/latest

[developer]: /developer

[commands]: /commands

[messenger]: reference.md#messenger

[minimessage]: minimessage.md

[placeholderapi]: /extensions/placeholderapi