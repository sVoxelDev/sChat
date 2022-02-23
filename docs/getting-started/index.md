# Getting Started

## Installation

**sChat** requires at least **Java 17** and [ProtocolLib][ProtocolLib] (*on the bukkit platform*).  
After these prerequisites are met [download sChat][download] and drop it into the `plugins/` folder. Restart the server and you are done.

!!! hint "Multi-Server Networks"
    If you have a multi server setup using **Bungeecord** or **Velocity**, then download the plugin for the relevant proxy platform and restart the proxy as well.

## Initial Configuration

sChat comes with two preconfigured channels: `global` and `team`. The latter can only be accessed by players that have the `schat.channel.team.join` permission.
It is [`protected`][config-protected]. Both channels are [`global`][config-global] and forward messages to all servers.

Here is a quick overview of the global channel configuration. Each setting is explained in great detail in the [Configuration][configuration-channel] section.

```yaml title="Global Channel Config"
channels:
  global:
    name: "<#189AB4>Global" # colors the name with the given hex formatted color
    settings:
      protected: false # the channel can be joined without a permission
      auto_join: true # the channel is added automatically to players
      global: true # messages are forwarded to all servers on the network
      forced: true # the channel cannot be left
```

[configuration-channel]: /configuration/channel
[config-global]: /configuration/channel#global
[config-protected]: /configuration/channel#protected
[download]: https://github.com/sVoxelDev/sChat/releases/latest
[ProtocolLib]: https://www.spigotmc.org/resources/protocollib.1997/
