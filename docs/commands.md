# Commands

Most of the UI elements in sChat can be interacted with by clicking on them. Under the hood they executed the comes that are listed below.

## Player Commands

Here is a list of player commands.

!!! note
    All players will have the `schat.player` [permission][permissions] by default granting them access to the following commands.

| Command                               | Alias                     | Permission | Description | Since |
|----------------------------------------|---------------------------| ---------- | ----------- | ----: |
| `/channel join <channel>`              | `/ch <channel>`           | `schat.player.channel.join` | Joins the given channel or sets it as active channel. | [:octicons-milestone-24: next][next] |
| `/channel leave <channel>`             | `/leave <channel>`        | `schat.player.channel.leave` | Leaves the given channel. | [:octicons-milestone-24: next][next] |
| `/channel message <channel> <message>` | `/ch <channel> <message>` | `schat.player.channel.quickmessage` | Sends a message to the given channel without switching to it. | [:octicons-milestone-24: next][next] |
|                                        |                           | | |
| `/tell <player> [message]`               | `/m`, `/w`, `/pm`, `/dm`  | `schat.player.directmessage` | Sends a message to the given player or opens the conversation. | [:octicons-milestone-24: next][next] |

## Admin Commands

As an admin you have access to the following commands.

| Commands | Alias | Permission | Description                                                                                                           | Since |
| -------- | ----- | ---------- |-----------------------------------------------------------------------------------------------------------------------| ---: |
| `/schat reload` | | `schat.admin.reload` | Reloads the sChat config and all channels that changed. This is non disruptive and will not touch unchanged channels. | [:octicons-milestone-24: next][next] |

[permissions]: /permissions
[next]: https://github.com/sVoxelDev/sChat/releases/latest
