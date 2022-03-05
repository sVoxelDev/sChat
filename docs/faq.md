# FAQ

Here are some of the most asked questions for sChat.

??? faq "Are HEX Colors supported?"
    Yes they are.  
    Take a look at the [MiniMessage Format][minimessage] to learn more.

??? faq "How can I add hover text to the player names?"
    You can define a custom [`message_format`][message-format] and use the [MiniMessage Syntax][minimessage] to display information in the hover event.  
    The use of [PlaceholderAPI][placeholderapi] placeholders is possible as well.

    ```yaml
    channels:
      global:
        name: Global
        settings:
          format:
            message_format: "<hover:show_text:'World: <aqua>%player_world%</aqua>'><source_display_name></hover>: <text>"
    ```

[minimessage]: configuration/minimessage
[message-format]: configuration/channels.md#message_format
[placeholderapi]: extensions/placeholderapi
