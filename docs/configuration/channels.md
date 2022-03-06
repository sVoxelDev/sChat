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

## `name`

[:octicons-milestone-24: 1.0.0][1.0.0] · [`minimessage`][minimessage] · :octicons-pin-24: `<key>` · :octicons-sync-24:

The name of the channel is displayed in the output of commands and the ui and parsed in the [MiniMessage][minimessage] format.

!!! note "Name Formatting"
    Use the [`format`](#format) options to control the color of the name based on the state of the channel.  
    The `active_color` is only used if the channel name has no color.

## `settings`

Every channel can have any or all of the following settings.  

!!! note
    Default values are marked with a :octicons-pin-24:.

### `priority`

[:octicons-milestone-24: 1.0.0][1.0.0] · `number` · :octicons-pin-24: `100` · :octicons-sync-24:

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

[:octicons-milestone-24: 1.0.0][1.0.0] · `boolean` · :octicons-pin-24: `false` · :octicons-sync-24:

Protected channels can only be joined by players that have the `schat.channel.<key>.join` permission.  

For example the `team` channel requires the `schat.channel.team.join` permission.

### `join_permission`

[:octicons-milestone-24: 1.0.0][1.0.0] · `string` · :octicons-pin-24: `schat.channel.<key>.join` · :octicons-sync-24:

You can override the required join permission with this setting.

!!! info
    This setting only works if `protected: true` is also set.

### `global`

[:octicons-milestone-24: 1.0.0][1.0.0] · `boolean` · :octicons-pin-24: `true` · :octicons-sync-24:

Messages that are sent to a `global` channel are forwarded with the [configured messenger][messenger].  

### `hidden`

[:octicons-milestone-24: 1.0.0][1.0.0] · `boolean` · :octicons-pin-24: `false` · :octicons-sync-24:

Hides the channel in commands, auto completion and listings.  
Does not prevent joining or leaving the channel.  
Players that are joined to the channel will see it in the channel tabs like all other channels.

### `auto_join`

[:octicons-milestone-24: 1.0.0][1.0.0] · `boolean` · :octicons-pin-24: `false` · :octicons-sync-24:

If enabled players will automatically join the channel when they join the server.  
Only channels the player has access to will be auto joined.

!!! note
    If the players leaves an `auto_join` channel and rejoins the server, the channel is joined again.  
    This option is therefor best combined with the `forced` setting.

### `forced`

[:octicons-milestone-24: 1.0.0][1.0.0] · `boolean` · :octicons-pin-24: `false` · :octicons-sync-24:

Forced channels cannot be left by players.

### `format`

The following settings are all formatting options that can be applied to the view of the channel.

They go under the normal `settings.format` section.

```yaml
channels:
  my_channel:
    name: My Channel
    settings:
      format:
        ... # <-- format options go here
```

--8<-- "docs/configuration/_tab_format_config.md"

[next]: https://github.com/sVoxelDev/sChat/releases/latest
[1.0.1]: https://github.com/sVoxelDev/sChat/releases/tag/v1.0.1
[1.0.0]: https://github.com/sVoxelDev/sChat/releases/tag/v1.0.0
[1.0.0]: https://github.com/sVoxelDev/sChat/releases/tag/1.0.0
[developer]: /developer
[commands]: /commands
[messenger]: reference.md#messenger
[minimessage]: minimessage.md
[color]: minimessage#color
[placeholderapi]: /extensions/placeholderapi
