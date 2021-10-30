package net.silthus.chat;

import org.bukkit.entity.Player;

public interface ChatSource {

    static ChatSource of(Player player) {
        return Chatter.of(player);
    }

    static ChatSource of(String identifier) {
        return new NamedChatSource(identifier);
    }

    static ChatSource of(String identifier, String displayName) {
        return new NamedChatSource(identifier, displayName);
    }

    String getIdentifier();

    String getDisplayName();
}
