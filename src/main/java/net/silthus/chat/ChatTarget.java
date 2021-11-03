package net.silthus.chat;

import net.silthus.chat.config.ChannelConfig;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface ChatTarget {

    static ChatTarget player(Player player) {
        return Chatter.of(player);
    }

    static ChatTarget nil() {
        return new NilChatTarget();
    }

    static Channel channel(String identifier) {
        return Channel.channel(identifier);
    }

    static Channel channel(String identifier, ChannelConfig config) {
        return Channel.channel(identifier, config);
    }

    static Console console() {
        return Console.console();
    }

    default Message sendMessage(String message) {
        return Message.message(message).to(this).send();
    }

    String getIdentifier();

    void sendMessage(Message message);

    Message getLastReceivedMessage();

    Collection<Message> getReceivedMessages();

    Collection<Channel> getSubscriptions();

    void subscribe(Channel channel);

    void unsubscribe(Channel channel);
}
