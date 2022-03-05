# PlaceholderAPI

The very popular [PlaceholderAPI plugin][papi-plugin] allows the use of *placeholders* in various text formats and messages.
A placeholder could be the current world, biome, player level, money or any other information provided by plugins supporting the PlaceholderAPI.

Placeholders can be used inside the [`message_format`][message-format] of [channels][channels].

!!! example
    The following examples displays the player's rank and money inside the hover.
    Colors have been omitted on puprose to make the snippet smaller.
    See the [MiniMessage Format][minimessage] documentation for more formatting options.

    ```yaml
    message_format: "<hover:show_text:'Rank: %vault_rank%<br>Money: %vault_eco_balance_formatted%'><source.display_name>: <text>"
    ```

[papi-plugin]: https://www.spigotmc.org/resources/placeholderapi.6245/
[message-format]: /configuration/channels#message_format
[channels]: /configuration/channels
[minimessage]: /configuration/minimessage