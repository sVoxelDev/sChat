package net.silthus.chat;

import org.bukkit.entity.Player;

public interface ChatSource {

    static ChatSource player(Player player) {
        return Chatter.of(player);
    }

    static ChatSource named(String identifier) {
        return new NamedChatSource(identifier);
    }

    static ChatSource named(String identifier, String displayName) {
        return new NamedChatSource(identifier, displayName);
    }

    static ChatSource nil() {
        return new NilChatSource();
    }

    String getIdentifier();

    String getDisplayName();

    default boolean isPlayer() {
        return false;
    }

    default Message.MessageBuilder message(String message) {
        return Message.message(this, message);
    }
}
