package net.silthus.chat;

import net.silthus.chat.config.ChannelConfig;
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

    static Channel channel(String identifier) {
        return Channel.channel(identifier);
    }

    static Channel channel(String identifier, ChannelConfig config) {
        return Channel.channel(identifier, config);
    }

    static ChatSource nil() {
        return new NilChatSource();
    }

    String getIdentifier();

    String getName();

    default boolean isPlayer() {
        return false;
    }

    default Message.MessageBuilder message(String message) {
        return Message.message(this, message);
    }
}
