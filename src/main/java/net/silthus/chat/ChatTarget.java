package net.silthus.chat;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface ChatTarget {

    static ChatTarget of(Player player) {
        return Chatter.of(player);
    }

    static ChatTarget empty() {
        return new EmptyChatTarget();
    }

    static Console console() {
        return Console.instance();
    }

    default void sendMessage(String message) {
        sendMessage(Message.of(message));
    }

    String getIdentifier();

    void sendMessage(Message message);

    Message getLastReceivedMessage();

    Collection<Message> getReceivedMessages();

    Collection<Channel> getSubscriptions();

    void subscribe(Channel channel);

    void unsubscribe(Channel channel);
}
